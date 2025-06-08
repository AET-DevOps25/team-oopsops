---
title: 'Problem Statement'
date: '2025-05-08'
---

# 📝 Project Deadlines

> ⚠️ **Deadline for submission:** 10 July 2025
> ⚠️ **Presentation:** 18 - 25 July 2025

---

## 📑 Table of Contents

1. [Main Functionality](#1-main-functionality)
2. [Intended Users](#2-intended-users)
3. [Integrating Generative AI](#3-integrating-generative-ai)
4. [User-Journey Scenarios](#4-user-journey-scenarios)

---

## 1. Main Functionality

> **What is the main functionality ?**

Automatic anonymization and summarization of user-submitted documents - PDFs at first, removing personal data such as names and addresses to ensure compliance with legal and privacy requirements.

Users simply upload a document and can either manually mark sections for redaction or let the AI analyze the text and suggest replacements, ensuring fast, consistent anonymization without leaking any real-world details.

> **Lower Priority:**

Voice based interaction with the documents.

---

## 2. Intended Users

> **Who are the intended users?**

- **Governments**
- **Universities**
- **Law firms**
- **Any organization** handling large volumes of sensitive documents

---

## 3. Integrating Generative AI

> **How do we use GenAI meaningfully?**

1. **Auto-Anonymization**
   - Automatically detect and redact or replace personal/sensitive information.
2. **AI-Powered Summaries**
   - Generate concise summaries to reduce manual reading effort and save users time in reviewing lengthy texts.

---

## 4. User-Journey Scenarios

### 4.1 Document Upload

- User uploads a file in one of: `PDF`, `DOCX`, `TXT`.

### 4.2 Auto-Anonymization

- The AI scans for personal information (names, addresses, phone numbers, etc.) and redacts or replaces it with placeholders.

### 4.3 Manual Review & Customization

- The user can select **additional fields** which were not auto-detected.
- Users can also **edit or rename placeholders** (e.g. change `“John Doe”` → `“Person A”`).

### 4.4 Summarization (Optional)

- The user can request a summary of the document, which the AI generates based on the content.

### 4.5 Download & Export

- User downloads the anonymized and/or summarized version in their desired format.

---

## 5 User Stories

### 5.0 User Story 0

1. As a user, I want to upload documents in various formats (PDF at first) so that I can work with the files I already have.
2. As a user, I want to anonymize my document so that I can share it without revealing sensitive information.
3. As a user, I want to manually review the anonymization process so that I can ensure all sensitive information is properly handled.
4. As a user, I want to be able to select the level of anonymization (e.g. light, medium, heavy) based on my needs.
5. As a user, I want to save and download the anonymized document in my desired format so that I can use it as needed.
6. As a user, I want to be able to generate a summary of the document so that I can quickly understand its content.
7. As a user, I want an archive of my documents so that I can keep track of my Original vs. anonymized files as well as the generated summary.

**(DO NOT include the following user stories in the final document)**

- As a user, I want to be able to chat with the document so that I can ask questions and get answers in real-time -> Chatbot
- As a user, I want to be able to talk to the document so that I can interact with it in a more natural way -> Voice based interaction
- As a user, I want to be able to ask questions about the document so that I can get specific information without reading the entire text.
- As a user, I want the system to be able to highlight sections of the document based on the agent conversation so that I can mark important information for later reference (Tool calling).
- As a user, I want to be able to add comments to the document so that I can provide feedback or notes for myself or others.

### 5.1 User Story 1 - Upload Document

> As a user, I want to upload documents in various formats (PDF at first) so that I can work with the files I already have.

- I want to be able to click a button to select files from my computer.
- I want to be able to drag and drop files into the upload area.
- I want to see a progress bar/loading indicator indicating the upload status.

### 5.2 User Story 2 - Auto-Anonymization

> As a user, I want to anonymize my document so that I can share it without revealing sensitive information.

- I want the system to automatically detect and redact personal information (e.g. names, addresses) from the document.
- I want to see a preview of the anonymized document before finalizing the process.
- I want to be able to select the level of anonymization (e.g. light, medium, heavy) based on my needs.

### 5.3 User Story 3 - Manual Review & Customization

> As a user, I want to manually review the anonymization process so that I can ensure all sensitive information is properly handled.

- I want to see sections of the document that have been anonymized - automatically highlighted.
- I want to see the original text on top of the anonymized text.
- The original text should be red and the anonymized text should be green.
- I want to be able to click on the highlighted sections to edit the anonymized text.
- I want to be able to have freedom to select any other text in the document to be anonymized.

### 5.4 User Story 4 - Level of Anonymization

> As a user, I want to be able to select the level of anonymization (e.g. light, medium, heavy) based on my needs.

- I want to see a slider or dropdown menu to select the level of anonymization.
- I want to see a description of what each level of anonymization means (e.g. light = names only, medium = names and addresses, heavy = all personal information).
- I want to be able to change the level of anonymization at any time during the process.
- I want to see a preview of the document with the selected level of anonymization applied.

**(Optional)**

- I want to be able to compare the original document with the anonymized document side by side.
- I want to be able to see the differences between the original and anonymized documents.

### 5.5 User Story 5 - Save & Download

> As a user, I want to be able to save and download the anonymized document in my desired format so that I can use it as needed.

- I want to be able to click a button to save the anonymized document - Local storage.
- I want to be able to download the anonymized document in my desired format.
- I want to see a confirmation message after the document is saved/downloaded.

### 5.6 User Story 6 - Summarization

> As a user, I want to be able to generate a summary of the document so that I can quickly understand its content.

- I want to be able to click a button to generate a summary of the original uploaded document (without having to anonymize it).
- I want to the summarize button to work exactly the same even after the anonymization process is complete.
- I want to be able to save the generated summary in my desired format and download it.
- I want to see a confirmation message after the summary is saved/downloaded.

### 5.7 User Story 7 - Archive

> As a user, I want to have an archive of my documents so that I can keep track of my Original vs. anonymized files as well as the generated summary.

- I want to see a list of all my uploaded documents in the archive.
- I want to have a side by side comparison view to see the original and anonymized versions of each document in the archive (separate page).
- I want to be able to search for specific documents in the archive, based on their file names.


### Summary Table

| ID   | Title                         | Acceptance Criteria                                                                                                                                                                       |
| ---- | ----------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| US-1 | Upload Document               | - Click to select files from computer<br>- Drag & drop into upload area<br>- Show upload progress bar                                                                                     |
| US-2 | Auto-Anonymization            | - Automatically detect & redact PII (names, addresses, etc.)<br>- Show preview of redactions<br>- Offer light/medium/heavy levels                                                         |
| US-3 | Manual Review & Customization | - Highlight auto-anonymized sections<br>- Show original text (in red) over anonymized text (in green)<br>- Allow click-to-edit anonymized text<br>- Allow selecting any text to anonymize |
| US-4 | Select Anonymization Level    | - Slider or dropdown to choose level<br>- Descriptions for light/medium/heavy<br>- Live preview updates on change                                                                         |
| US-5 | Save & Download               | - “Save” button stores anonymized document locally<br>- “Download” button exports in chosen format (PDF, DOCX, TXT)<br>- Confirmation message on success                                  |
| US-6 | Generate Summary              | - “Summarize” button works before or after anonymization<br>- Displays summary of original document<br>- Save/download summary in chosen format<br>- Confirmation message on success      |
| US-7 | Document Archive              | - List all uploaded documents (original, anonymized, summary)<br>- Side-by-side view of original vs. anonymized<br>- Search by file name in archive                                       |
