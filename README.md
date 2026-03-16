## Architecture

This project follows **Feature-based architecture**.
Each feature is self-contained with its own
entity, repository, service, controller and DTOs.

com.axiora.pec
├── user        → User management
├── goal        → Goal management  
├── kpi         → KPI management
├── rule        → Rule engine
├── evaluation  → Evaluation engine
├── scorecard   → Scorecard & reporting
└── common      → Shared: config, exceptions, audit