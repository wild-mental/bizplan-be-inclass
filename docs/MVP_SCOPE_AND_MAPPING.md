# MVP Scope and Requirement Mapping

This document maps the SRS requirements to the MVP scope, providing the basis for Task extraction.

## 1. MVP Scope Definition (from SRS 1.2.1)

| Feature | Description | REQ-FUNC IDs | Priority |
| :--- | :--- | :--- | :--- |
| **F1** | Government/Bank Submission Wizard | REQ-FUNC-001, 002, 007, 013 | Must |
| **F2** | Template Compatibility + Export (HWP/PDF) | REQ-FUNC-011 | Must |
| **F3** | Financial Automation Engine | REQ-FUNC-006, 009, 012 | Must |
| **F4** | AI Draft Generation + Editor | REQ-FUNC-003, 004, 005, 014 | Must |
| **F5** | PMF Diagnostic Framework | REQ-FUNC-008, 010 | Should |
| **F6** | On-premise/Private Cloud (Design Only) | REQ-FUNC-015, 016 | Post-MVP (Design Only) |

## 2. Requirement to Component Mapping

| REQ ID | Title | Components | APIs (SRS 6.1) | Entities (SRS 6.2) |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-FUNC-001** | Template List | FE, BE | `POST /projects` (selection) | Project |
| **REQ-FUNC-002** | Wizard Logic | FE, BE | `POST /projects/{id}/wizard/steps` | Project |
| **REQ-FUNC-003** | Draft Generation | FE, BE, AI-Engine | `POST /.../business-plan:generate` | BusinessPlanDocument |
| **REQ-FUNC-004** | Section AI Help | FE, BE, AI-Engine | `POST /.../business-plan:generate` (partial) | BusinessPlanDocument |
| **REQ-FUNC-005** | Checklist | FE, BE, AI-Engine | `POST /.../business-plan:checklist` | BusinessPlanDocument |
| **REQ-FUNC-006** | Consistency Check | FE, BE, Logic-Engine | `POST /.../consistency-check` | FinancialModel, PMFReport |
| **REQ-FUNC-007** | Mandatory Check | FE, BE | (Validation Logic) | Project |
| **REQ-FUNC-008** | PMF Report | FE, BE, AI-Engine | `POST /.../pmf-report:generate` | PMFReport |
| **REQ-FUNC-009** | Unit Economics | FE, BE | `GET /.../unit-economics` | FinancialModel |
| **REQ-FUNC-010** | PMF Data Validation | FE, BE | (Validation Logic) | PMFReport |
| **REQ-FUNC-011** | Export (HWP/PDF) | FE, BE, Converter | `GET /.../export` | BusinessPlanDocument |
| **REQ-FUNC-012** | Financial Engine | FE, BE, Logic-Engine | `POST /.../financials:generate` | FinancialModel |
| **REQ-FUNC-013** | Auto-save | FE, BE | `POST /projects/{id}/wizard/steps` | Project |
| **REQ-FUNC-014** | View Modes | FE | - | - |

## 3. Non-Functional Requirements (SRS 4.2)

| ID | Category | Scope |
| :--- | :--- | :--- |
| **REQ-NF-001** | Performance | Wizard Response Time |
| **REQ-NF-002** | Performance | Document Gen Response Time |
| **REQ-NF-003** | Availability | SLA 99.5% |
| **REQ-NF-004** | Reliability | Error Rate |
| **REQ-NF-005** | Reliability | Auto-save Interval |
| **REQ-NF-006** | Security | Data Encryption (At Rest) |
| **REQ-NF-007** | Security | TLS (In Transit) |
| **REQ-NF-008** | Security | Data Isolation |
| **REQ-NF-009** | Scalability | 1000 Concurrent Users |
| **REQ-NF-010** | Maintainability | Template Update Lead Time |
| **REQ-NF-011** | Cost | AI Token Limit |
| **REQ-NF-012** | Monitoring | Alerting Rules |
| **REQ-NF-013~017** | Business KPI | TTV, Activation, NPS, etc. |
| **REQ-NF-018** | Resilience | Backup/Recovery |

