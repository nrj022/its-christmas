# It's Christmas! Create Your Own 3D Card.
## Testing

This project includes comprehensive unit test coverage for the refactored codebase. Tests are written using JUnit 4, MockK, and Turbine for Flow testing.

### Test Organization
- **core/data/src/test**: Tests for data layer including mappers and repository implementations
- **core/domain/src/test**: Tests for domain models and business logic
- **feature/card/src/test**: Tests for feature-specific ViewModels and UI logic

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:data:test
```

See [TEST_COVERAGE.md](TEST_COVERAGE.md) for detailed test coverage information.