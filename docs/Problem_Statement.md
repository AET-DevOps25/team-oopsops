---
title: "Problem Statement"
date: "2025-05-08"
---

# 📝 Problem Statement

> ⚠️ **Deadline for submission:** 09 May 2025  

---

## 📑 Table of Contents

1. [Main Functionality](#1-main-functionality)  
2. [Intended Users](#2-intended-users)  
3. [Integrating Generative AI](#3-integrating-generative-ai)  
4. [User-Journey Scenarios](#4-user-journey-scenarios)  

---

## 1. Main Functionality

> **What is the main functionality ?**

Our service provides automatic anonymization and summarization of user-submitted documents, removing personal data such as names and addresses to ensure compliance with legal and privacy requirements.  

Users simply upload a document and can either manually mark sections for redaction or let the AI analyze the text and suggest replacements, ensuring fast, consistent anonymization without leaking any real-world details.

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
