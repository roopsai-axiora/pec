# Postman Setup

This folder contains a clean Postman setup for exercising the PEC API.

Files:
- `PEC.postman_collection.json`
- `PEC.local.postman_environment.template.json`

## Import

1. Open Postman.
2. Import the collection file.
3. Import the environment template file.
4. Select the `PEC Local` environment.

## Important Variables

- `baseUrl`
- `adminToken`
- `managerToken`
- `employeeToken`
- `employeeId`
- `goalId`
- `evaluationId`
- `period`

## Recommended Run Order

1. `Auth / Register Admin`
2. `Auth / Register Manager`
3. `Auth / Register Employee`
4. `Auth / Login Admin`
5. `Auth / Login Manager`
6. `Auth / Login Employee`
7. `Users / Get Employees`
8. Copy the returned employee `id` into the environment variable `employeeId`
9. `Goals / Create Goal For Employee`
10. `Rules / Create High Achiever Rule`
11. `Rules / Get Active Rules`
12. `KPIs / Submit KPI For Goal`
13. `Evaluations / Run Evaluation`
14. `Evaluations / Get Evaluation By Id`
15. `Evaluations / Download Scorecard PDF`

## Note About `employeeId`

Use `Users / Get Employees` after manager login to fetch employee records, then copy the target employee's `id` into the Postman environment variable `employeeId`.

## Automatically Captured Variables

The collection stores these values automatically:

- `adminToken` after admin login
- `managerToken` after manager login
- `employeeToken` after employee login
- `goalId` after goal creation
- `evaluationId` after evaluation creation

## First-Time Postman Usage

1. Import both JSON files into Postman.
2. Open the environment selector in the top-right corner.
3. Choose `PEC Local`.
4. Start with the `Auth` folder and click `Send` on each request in order.
5. After login requests, Postman automatically stores tokens in the environment.
6. Run `Users / Get Employees`, copy the employee `id`, and paste it into the environment variable `employeeId`.
7. Continue with `Goals`, `Rules`, `KPIs`, and `Evaluations`.

## Editing Environment Variables

In Postman:

1. Click the eye icon in the top-right.
2. Open the active environment.
3. Update values like `employeeId`, `goalId`, or `evaluationId`.
4. Save the environment.
