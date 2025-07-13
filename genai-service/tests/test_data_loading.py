"""Test to verify test data loading."""

import json
from pathlib import Path


def test_json_loading():
    """Test if test data files can be loaded."""
    test_data_dir = Path(__file__).parent / "test_data"

    # Test scenarios loading
    scenarios_file = test_data_dir / "test_scenarios.json"
    print(f"Scenarios file exists: {scenarios_file.exists()}")

    if scenarios_file.exists():
        with open(scenarios_file, "r") as f:
            data = json.load(f)
        print(f"Scenarios data: {data}")
        assert "scenarios" in data
        assert len(data["scenarios"]) > 0

    # Test mock responses loading
    mock_file = test_data_dir / "mock_responses.json"
    print(f"Mock responses file exists: {mock_file.exists()}")

    if mock_file.exists():
        with open(mock_file, "r") as f:
            data = json.load(f)
        print(f"Mock responses keys: {list(data.keys())}")


if __name__ == "__main__":
    test_json_loading()
