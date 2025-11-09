# Morphos

**Morphos** is a Java library for database schema introspection and structural modeling.
It reflects the internal structure of your database, exposing tables, columns, keys, and constraints as a navigable object model.

---

## Overview

**Morphos** allows developers to introspect relational databases and transform their schema into a structured, programmatic model.  

With Morphos, you can discover tables, columns, primary and foreign keys, and constraints across popular databases, generating a clean object model suitable for code generation, analysis, or further transformations.

Key features:

- Reflection of database catalogs and schemas
- Full model of tables, columns, primary and foreign keys, and constraints
- Extensible to multiple database dialects
- Lightweight and easy to integrate with Java projects

---

## Getting Started

### Maven Dependency

Add Morphos to your Maven project:

```xml
<dependency>
    <groupId>org.archmix</groupId>
    <artifactId>morphos-oss</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
