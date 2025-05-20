# E-Commerce System

## Overview
This Java-based E-Commerce System provides a command-line interface for managing products, customers, and orders. The system includes separate functionality for administrators and customers with features like user authentication, profile management, product browsing, and statistical reporting.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [System Architecture](#system-architecture)
- [Usage Guide](#usage-guide)
  - [Getting Started](#getting-started)
  - [Customer Functions](#customer-functions)
  - [Administrator Functions](#administrator-functions)
- [Data Management](#data-management)
- [Reporting](#reporting)

## Features
- **User Management**: Registration, login, profile viewing and updating
- **Product Management**: Browse products, search by keyword
- **Order History**: View past orders
- **Admin Tools**: Generate test data, view all users and orders
- **Statistical Analysis**: Generate consumption and product analysis charts

## Installation
1. Clone the repository to your local machine
2. Ensure you have JDK 8 or higher installed
3. Compile the Java files with `javac ECommerceSystem.java`
4. Run the application with `java ECommerceSystem`

## System Architecture
The system follows an object-oriented architecture with:
- **Model**: User, Admin, Customer, Product, Order classes
- **Operation**: Logic for user, admin, customer, product, and order operations
- **Utilities**: File handling and JSON parsing
- **Main System**: ECommerceSystem class coordinates all components

## Usage Guide

### Getting Started

#### Starting the Application
Run the ECommerceSystem class to display the main menu:
```
====== E-Commerce System ======
1. Login
2. Register
3. Quit
===============================
```

#### Navigation Tips
- Enter the menu option number for selection (e.g., "1" to login)
- For pagination, use "n" (next page), "p" (previous page), "b" (back)

#### Registration
1. From main menu, enter "2"
2. Input format: `username password email mobile`
   - Example: `customer123 Pass123 customer@example.com 0412345678`
   
**Validation Requirements:**
- Username: 5+ characters, letters and underscore only
- Password: 5+ characters with at least one letter and one digit
- Email: Valid email format
- Mobile: Must start with 04 or 03 and have 10 digits total

#### Login
1. From main menu, enter "1"
2. Input format: `username password`
   - Example: `customer123 Pass123` or `admin admin123` (default admin)

### Customer Functions

After customer login, you'll see:
```
====== Customer Menu ======
1. Show profile
2. Update profile
3. Show products (you can input "3 keyword" or "3")
4. Show history orders
5. Generate all consumption figures
6. Logout
===========================
```

#### View Profile
Enter "1" to display your profile information in JSON format

#### Update Profile
1. Enter "2"
2. Input format: `attributeName newValue`
   - Valid attributes: username, userpassword, useremail, usermobile
   - Example: `username newUsername` or `usermobile 0423456789`

#### Browse Products
- Enter "3" to view all products
- Enter "3 keyword" to search for specific products (e.g., `3 iPhone`)
- Navigate pages with "n", "p", or "b"

#### View Order History
1. Enter "4" to see your order history
2. Navigate through pages with "n", "p", or "b"

#### Generate Consumption Figures
Enter "5" to create a chart of your monthly consumption (saved to "data/figure" folder)

#### Logout
Enter "6" to return to the main menu

### Administrator Functions

After admin login (default: `admin admin123`), you'll see:
```
====== Admin Menu ======
1. Show products
2. Add customers
3. Show customers
4. Show orders
5. Generate test data
6. Generate all statistical figures
7. Delete all data
8. Logout
========================
```

#### Show Products
Enter "1" to view products with pagination (navigate with "n", "p", "b")

#### Add Customers
1. Enter "2"
2. Input format: `username password email mobile`
   - Same validation as registration

#### Show Customers
Enter "3" to view all customers with pagination

#### Show Orders
Enter "4" to view all system orders

#### Generate Test Data
Enter "5" to create sample customers and orders

#### Generate Statistical Figures
Enter "6" to create charts in "data/figure" folder:
- Product category distribution
- Discount distribution
- Likes count by category
- Discount vs likes correlation
- Monthly consumption data
- Top 10 best-selling products

#### Delete All Data
1. Enter "7"
2. Confirm with "yes" to delete all system data

#### Logout
Enter "8" to return to the main menu

## Data Management
- All data is stored in `src/Resources/data.txt`
- Data persists between application sessions
- Test data can be generated or deleted through admin functions

## Reporting
- Statistical figures are generated as image files
- All charts are saved to the `data/figure` folder
- Charts include consumption analysis, product performance, and category distributions
