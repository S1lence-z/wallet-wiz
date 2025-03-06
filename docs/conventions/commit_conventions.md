## 1. Commit Message Guidelines (Conventional Commits)

We use [Conventional Commits](https://www.conventionalcommits.org/).

### **1.1 Commit Format**

```
<type>(optional scope): <description>

[optional body]

[optional footer]
```

### **1.2 Commit Types**

| Type       | Meaning                                                |
| ---------- | ------------------------------------------------------ |
| `feat`     | A new feature                                          |
| `fix`      | A bug fix                                              |
| `docs`     | Documentation changes                                  |
| `style`    | Formatting, missing semicolons, etc. (no code changes) |
| `refactor` | Code restructuring without feature changes             |
| `test`     | Adding or updating tests                               |
| `chore`    | Maintenance tasks (e.g., CI, dependencies)             |
| `perf`     | Performance improvements                               |

---

## 2. Branch Naming Conventions

We follow the `type/name-of-branch` format for branch names.

### **2.1 Branch Types**

| Type       | Description                                 | Example                     |
| ---------- | ------------------------------------------- | --------------------------- |
| `feat`     | New feature implementation                  | `feat/user-authentication`  |
| `fix`      | Bug fixes                                   | `fix/login-issue`           |
| `docs`     | Documentation updates                       | `docs/api-reference`        |
| `style`    | Code styling, formatting changes            | `style/code-cleanup`        |
| `refactor` | Code refactoring without functional changes | `refactor/database-layer`   |
| `test`     | Adding or modifying tests                   | `test/signup-validation`    |
| `chore`    | CI/CD, dependencies, and maintenance        | `chore/update-dependencies` |
| `perf`     | Performance improvements                    | `perf/query-optimization`   |

### **2.2 Additional Rules**

- Use lowercase and hyphens (`-`) instead of spaces.
- Keep branch names concise but descriptive.
- If related to an issue or ticket, append the ID at the end (e.g., `feat/payment-integration-123`).

Would you like to add any custom rules specific to your workflow?
