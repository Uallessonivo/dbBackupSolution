# Backup Management System with Java and Spring Boot using GCP Storage

This project implements a backup management system in Java with Spring Boot, designed to automate the backup process of files generated daily by the database management system (DBMS). It compresses the backups, uploads them to [Google Cloud Storage (GCS)](https://cloud.google.com/storage), and manages backup retention.

## Architecture

The solution consists of the following components:

- **Java Backup Agent**: A Java program with Spring Boot that performs tasks such as identifying the latest backup, compressing files, and uploading to Google Cloud Storage.

- **Cloud Storage Solution**: We will use [Google Cloud Storage (GCS)](https://cloud.google.com/storage) to store the backups.

## Development

### Environment Setup

Make sure to set up the development environment with the following dependencies:

- Installed Java and Spring Boot
- Access to [Google Cloud Storage (GCS)](https://cloud.google.com/storage)
- Knowledge of the API or SDK of [Google Cloud Storage (GCS)](https://cloud.google.com/storage)

### Backup Agent

The backup agent is responsible for:

- Identifying the folder with the latest backup.
- Compressing backup files, if necessary.
- Connecting to the [Google Cloud Storage (GCS)](https://cloud.google.com/storage) account.

### Task Scheduling

Schedule the execution of the backup agent to run daily, after generating database backups.

### Security and Audit

(Coming soon).

### Monitoring and Scalability

(Coming soon).

