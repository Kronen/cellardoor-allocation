# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [0.3.1] - 2025-11-30

### Added
- OpenAPI 3.0 documentation for all REST endpoints
- Integration tests for batch allocation scenarios
- GitHub Actions workflow for automated releases
- CHANGELOG.md to track all changes

### Changed
- Upgraded to cellardoor-bom 1.0.0
- Improved error handling in allocation service
- Enhanced test coverage for edge cases
- Optimized build configuration

### Fixed
- Resolved build issues in CI workflow
- Fixed race condition in concurrent allocations
- Addressed security vulnerabilities in dependencies
- Corrected OpenAPI schema validations

### Removed
- Deprecated API endpoints
- Unused dependencies
- Redundant configuration files

## [0.3.0] - 2025-11-15

### Added
- Initial implementation of allocation service
- Basic domain models for batches and order lines
- REST API endpoints for allocation operations
- MongoDB integration for persistence

### Changed
- [Refactored domain model for better consistency
- Improved error messages and logging
- Enhanced API documentation

## [0.2.0] - 2025-10-30

### Added
- Project skeleton with Maven multi-module setup
- Basic domain model implementation
- Initial test infrastructure
- CI pipeline configuration]()

---

[Unreleased]: https://github.com/Kronen/cellardoor-allocation/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/Kronen/cellardoor-allocation/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/Kronen/cellardoor-allocation/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/Kronen/cellardoor-allocation/releases/tag/v0.2.0
