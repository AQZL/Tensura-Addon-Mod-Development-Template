#requires -Version 5.1
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$Destination,
    [Parameter(Mandatory = $true)][string]$ModId,
    [Parameter(Mandatory = $true)][string]$ModName,
    [Parameter(Mandatory = $true)][string]$Package,
    [Parameter(Mandatory = $true)][string]$Authors,
    [Alias('Version')][string]$ModVersion = '0.1.0'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$utf8 = New-Object System.Text.UTF8Encoding($false)
$keywords = ('abstract assert boolean break byte case catch char class const continue ' +
    'default do double else enum extends final finally float for goto if implements ' +
    'import instanceof int interface long native new package private protected public ' +
    'return short static strictfp super switch synchronized this throw throws transient ' +
    'try void volatile while _ true false null').Split(' ')
function Assert-Identity([string]$IdValue, [string]$PackageValue) {
    if ($IdValue -cnotmatch '^[a-z][a-z0-9_]{1,63}$' -or $keywords -ccontains $IdValue) {
        throw 'ModId must be 2-64 lowercase letters, digits or underscores, start with a letter, and not be a Java keyword.'
    }
    if ($PackageValue -cnotmatch '^[A-Za-z_][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)*$') {
        throw 'Package must contain Java identifiers separated by dots (letters, digits and underscores only).'
    }
    foreach ($part in $PackageValue.Split('.')) {
        if ($keywords -ccontains $part) { throw "Package contains a Java keyword: $part" }
    }
}
Assert-Identity $ModId $Package
$values = [ordered]@{
    mod_id = $ModId; mod_name = $ModName; mod_group_id = $Package
    mod_authors = $Authors; mod_version = $ModVersion
}
foreach ($value in $values.Values) {
    if ([string]::IsNullOrWhiteSpace($value) -or $value -match '[\x00-\x1f\x7f"''\\]') {
        throw 'Metadata values must be nonempty and contain no control characters, quotes or backslashes.'
    }
}
if ($ModVersion -cnotmatch '^[0-9A-Za-z][0-9A-Za-z.+_-]*$') { throw 'Invalid ModVersion.' }

$templatePath = [IO.Path]::GetFullPath($PSScriptRoot)
$destinationPath = [IO.Path]::GetFullPath(
    $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($Destination))
$templateBoundary = $templatePath.TrimEnd([char[]]'\/') + [IO.Path]::DirectorySeparatorChar
$destinationBoundary = $destinationPath.TrimEnd([char[]]'\/') + [IO.Path]::DirectorySeparatorChar
$comparison = [StringComparison]::OrdinalIgnoreCase
if ($destinationBoundary.StartsWith($templateBoundary, $comparison) -or
    $templateBoundary.StartsWith($destinationBoundary, $comparison)) {
    throw 'Destination must be outside the template and cannot be the template or any of its parents.'
}
if (Test-Path -LiteralPath $destinationPath) { throw "Destination already exists: $destinationPath" }
$ancestor = Split-Path -Parent $destinationPath
while ($ancestor) {
    if (Test-Path -LiteralPath $ancestor) {
        $ancestorItem = Get-Item -LiteralPath $ancestor -Force
        if (-not $ancestorItem.PSIsContainer -or
            ($ancestorItem.Attributes -band [IO.FileAttributes]::ReparsePoint)) {
            throw "Destination parent must be an ordinary directory: $ancestor"
        }
    }
    $ancestor = Split-Path -Parent $ancestor
}

function ConvertTo-PropertyValue([string]$Value) {
    $result = New-Object System.Text.StringBuilder
    foreach ($character in $Value.ToCharArray()) {
        if ([int]$character -gt 126) { [void]$result.Append(('\u{0:X4}' -f [int]$character)) }
        elseif ('\ =:#!'.Contains([string]$character)) { [void]$result.Append('\').Append($character) }
        else { [void]$result.Append($character) }
    }
    return $result.ToString()
}
$properties = [IO.File]::ReadAllText((Join-Path $templatePath 'gradle.properties'))
$sourceValues = @{}
foreach ($key in $values.Keys) {
    $propertyMatches = [regex]::Matches($properties, ('(?m)^' + $key + '[ \t]*=([^\r\n]*)'))
    if ($propertyMatches.Count -ne 1) { throw "Expected exactly one $key property in gradle.properties." }
    $sourceValues[$key] = $propertyMatches[0].Groups[1].Value.Trim()
}
$oldId = $sourceValues.mod_id
$oldPackage = $sourceValues.mod_group_id
$oldVersion = $sourceValues.mod_version
Assert-Identity $oldId $oldPackage
$properties = [regex]::Replace($properties,
    '(?m)^(mod_id|mod_name|mod_group_id|mod_authors|mod_version)[ \t]*=[^\r\n]*',
    [Text.RegularExpressions.MatchEvaluator]{ param($hit)
        $key = $hit.Groups[1].Value
        return $key + '=' + (ConvertTo-PropertyValue $values[$key])
    })

# Replace all identities in one pass: newly inserted names must never be replaced again.
$identityRules = [ordered]@{
    Artifact = @(($oldId + '-' + $oldVersion + '.jar'), ($ModId + '-' + $ModVersion + '.jar'))
    Package = @($oldPackage, $Package)
    SlashPackage = @($oldPackage.Replace('.', '/'), $Package.Replace('.', '/'))
    BackslashPackage = @($oldPackage.Replace('.', '\'), $Package.Replace('.', '\'))
    Namespace = @($oldId, $ModId)
}
$patternParts = foreach ($ruleName in $identityRules.Keys) {
    '(?<' + $ruleName + '>(?<![A-Za-z0-9_$])' + [regex]::Escape($identityRules[$ruleName][0]) + '(?![A-Za-z0-9_$]))'
}
$identityRegex = [regex]::new(($patternParts -join '|'))
function Convert-TemplateText([string]$Value) {
    return $identityRegex.Replace($Value, [Text.RegularExpressions.MatchEvaluator]{ param($hit)
        foreach ($ruleName in $identityRules.Keys) {
            if ($hit.Groups[$ruleName].Success) { return $identityRules[$ruleName][1] }
        }
        return $hit.Value
    })
}
function Convert-RelativePath([string]$Relative) {
    $javaPattern = '^(src/[^/]+/java/)' + [regex]::Escape($oldPackage.Replace('.', '/')) + '(?=/|$)'
    $resourcePattern = '^(src/[^/]+/(?:resources|templates)/(?:assets|data)/)' + [regex]::Escape($oldId) + '(?=/|$)'
    $mapped = [regex]::Replace($Relative, $javaPattern, ('${1}' + $Package.Replace('.', '/')))
    return [regex]::Replace($mapped, $resourcePattern, ('${1}' + $ModId))
}
$entryPath = Join-Path $templatePath ('src/main/java/' + $oldPackage.Replace('.', '/') + '/TensuraAddon.java')
$entryText = [IO.File]::ReadAllText($entryPath)
if (-not [regex]::IsMatch($entryText, ('(?m)^\s*package\s+' + [regex]::Escape($oldPackage) + '\s*;')) -or
    -not [regex]::IsMatch($entryText, ('\bMOD_ID\s*=\s*"' + [regex]::Escape($oldId) + '"\s*;'))) {
    throw 'The template entry point package and MOD_ID must agree with gradle.properties.'
}

# Plan every output before writing. JARs, lib manifests and this script are copied unchanged.
$plan = [Collections.Generic.List[object]]::new()
$targets = [Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
function Add-CopyPlan([string]$SourcePath, [string]$Relative) {
    if ($Relative -eq 'src/generated') { return }
    $item = Get-Item -LiteralPath $SourcePath -Force
    if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw "Template contains a junction or symlink: $SourcePath" }
    if ($item.PSIsContainer) {
        foreach ($child in Get-ChildItem -LiteralPath $SourcePath -Force) {
            Add-CopyPlan $child.FullName ($Relative + '/' + $child.Name)
        }
        return
    }
    $targetPath = [IO.Path]::GetFullPath((Join-Path $destinationPath (Convert-RelativePath $Relative)))
    if (-not $targetPath.StartsWith($destinationBoundary, $comparison) -or -not $targets.Add($targetPath)) {
        throw "Unsafe or duplicate output path: $targetPath"
    }
    $text = $null
    if ($Relative -eq 'gradle.properties') { $text = $properties }
    elseif ($Relative -eq 'README.md' -or ($Relative.StartsWith('docs/') -and $item.Extension -eq '.md') -or
        ($Relative.StartsWith('src/') -and @('.java', '.json') -contains $item.Extension)) {
        $text = Convert-TemplateText ([IO.File]::ReadAllText($SourcePath))
    }
    $plan.Add([pscustomobject]@{ Source = $SourcePath; Target = $targetPath; Text = $text })
}
$whitelist = @('gradle', 'lib', 'src', 'docs', 'gradlew', 'gradlew.bat', 'build.gradle',
    'settings.gradle', 'gradle.properties', '.gitignore', '.gitattributes', 'README.md', 'New-Addon.ps1')
foreach ($name in $whitelist) { Add-CopyPlan (Join-Path $templatePath $name) $name }

# Only the new directory is written; no files or directories are moved or deleted.
New-Item -ItemType Directory -Path $destinationPath -ErrorAction Stop | Out-Null
foreach ($operation in $plan) {
    [void][IO.Directory]::CreateDirectory((Split-Path -Parent $operation.Target))
    if ($null -eq $operation.Text) {
        Copy-Item -LiteralPath $operation.Source -Destination $operation.Target
    } else {
        [IO.File]::WriteAllText($operation.Target, $operation.Text, $utf8)
    }
}
Write-Output "Created addon: $destinationPath"
Write-Output 'Next: open the new project, then run .\gradlew.bat build with Java 21.'
