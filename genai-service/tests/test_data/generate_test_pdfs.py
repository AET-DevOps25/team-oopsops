"""Script to generate test PDF files for testing."""

from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
import os
from pathlib import Path


def create_sample_pdf():
    """Create a sample PDF with personal information for testing."""
    filename = "sample.pdf"
    c = canvas.Canvas(filename, pagesize=letter)

    # Add content with various types of PII
    content = [
        "CONFIDENTIAL DOCUMENT",
        "",
        "Personal Information:",
        "Name: John Doe",
        "Address: 123 Main Street, Anytown, USA 12345",
        "Phone: (555) 123-4567",
        "Email: john.doe@email.com",
        "SSN: 123-45-6789",
        "Date of Birth: 01/15/1980",
        "",
        "Financial Information:",
        "Account Number: 9876543210",
        "Credit Card: 4532-1234-5678-9012",
        "Bank: First National Bank",
        "",
        "Medical Information:",
        "Patient ID: MED-789456",
        "Diagnosis: Confidential medical condition",
        "",
        "This document contains sensitive personal information",
        "that should be properly anonymized before sharing.",
    ]

    y_position = 750
    for line in content:
        c.drawString(100, y_position, line)
        y_position -= 20

    c.save()
    print(f"Created {filename}")


def create_minimal_pdf():
    """Create a minimal PDF for basic testing."""
    filename = "minimal.pdf"
    c = canvas.Canvas(filename, pagesize=letter)
    c.drawString(100, 750, "Hello World")
    c.drawString(100, 730, "This is a minimal test document.")
    c.save()
    print(f"Created {filename}")


def create_empty_pdf():
    """Create an empty PDF for edge case testing."""
    filename = "empty.pdf"
    c = canvas.Canvas(filename, pagesize=letter)
    c.save()
    print(f"Created {filename}")


def create_large_pdf():
    """Create a large PDF for performance testing."""
    filename = "large_document.pdf"
    c = canvas.Canvas(filename, pagesize=letter)

    # Add multiple pages with repeated content
    for page in range(10):
        y_position = 750
        c.drawString(100, y_position, f"PAGE {page + 1}")
        y_position -= 30

        # Add repeated personal information
        for i in range(30):
            c.drawString(
                100,
                y_position,
                f"Person {i}: John Doe {i}, SSN: 123-45-{6789 + i}",
            )
            y_position -= 15
            if y_position < 100:
                break

        if page < 9:  # Don't add page break on last page
            c.showPage()

    c.save()
    print(f"Created {filename}")


def create_mixed_content_pdf():
    """Create PDF with mixed sensitive and non-sensitive content."""
    filename = "mixed_content.pdf"
    c = canvas.Canvas(filename, pagesize=letter)

    content = [
        "BUSINESS REPORT - 2024",
        "",
        "Executive Summary:",
        "This report contains analysis of our business operations.",
        "Public information about market trends and forecasts.",
        "",
        "CONFIDENTIAL SECTION:",
        "Employee Information:",
        "- Jane Smith, Manager, SSN: 987-65-4321",
        "- Bob Johnson, Developer, SSN: 456-78-9123",
        "",
        "Customer Data:",
        "- Customer ID: CUST-001, Email: customer@company.com",
        "- Phone: (555) 987-6543",
        "",
        "Financial Data:",
        "- Revenue: $1,250,000",
        "- Account: 1234567890",
        "",
        "Public Conclusions:",
        "Market analysis shows positive trends.",
        "Recommendations for future growth.",
    ]

    y_position = 750
    for line in content:
        if "CONFIDENTIAL" in line:
            # Make confidential sections stand out
            c.setFont("Helvetica-Bold", 12)
        else:
            c.setFont("Helvetica", 10)

        c.drawString(100, y_position, line)
        y_position -= 20

    c.save()
    print(f"Created {filename}")


def create_corrupted_pdf():
    """Create a corrupted PDF for error testing."""
    filename = "corrupted.pdf"
    with open(filename, 'wb') as f:
        f.write(b"This is not a valid PDF file content")
    print(f"Created {filename}")


if __name__ == "__main__":
    # Ensure we're in the test_data directory
    script_dir = Path(__file__).parent
    os.chdir(script_dir)
    
    print("Generating test PDF files in test_data directory...")
    print(f"Current working directory: {os.getcwd()}")
    
    create_sample_pdf()
    create_minimal_pdf()
    create_empty_pdf()
    create_large_pdf()
    create_mixed_content_pdf()
    create_corrupted_pdf()
    
    print("All test PDF files generated successfully!")
    print("Generated files:")
    for file in Path(".").glob("*.pdf"):
        print(f"  - {file.name}")
