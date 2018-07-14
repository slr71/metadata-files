# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of
[keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [1.0.2]
### Changed
- Fixed a problem with the `doi:` prefix causing schema validation errors.
- Refactored the XML schema validation code for readability.

## [1.0.1]
### Changed
- Fixed the XML schema validation in the unit tests, which wasn't actually validating against the schema.
- Fixed several differences between DataCite versions 4.1 and 3.1, which weren't caught earlier because of the schema
  validation bug.

## 1.0.0
### Added
- Support for DataCite metadata files generated from CyVerse DOI request metadata.

[Unreleased]: https://github.com/cyverse-de/metadata-files/compare/1.0.2...HEAD
[1.0.2]: https://github.com/cyverse-de/metadata-files/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/cyverse-de/metadata-files/compare/1.0.0...1.0.1
