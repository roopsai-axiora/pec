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
- `orgName`
- `adminToken`
- `managerToken`
- `managerTwoToken`
- `employeeToken`
- `employeeTwoToken`
- `employeeId`
- `employeeTwoId`
- `goalId`
- `goalTwoId`
- `evaluationId`
- `period`

## Recommended Run Order

1. `Auth / Register Admin`
2. `Auth / Register Manager One`
3. `Auth / Register Manager Two`
4. `Auth / Register Employee One`
5. `Auth / Register Employee Two`
6. `Auth / Login Admin`
7. `Auth / Login Manager One`
8. `Auth / Login Manager Two`
9. `Auth / Login Employee One`
10. `Auth / Login Employee Two`
11. `Users / Get Employees`
12. Copy returned ids into `employeeId` and `employeeTwoId`
13. `Goals / Create Goal For Employee One`
14. `Goals / Create Goal For Employee Two`
15. `Rules / Create High Achiever Rule`
16. `Rules / Get Active Rules`
17. `KPIs / Submit KPI For Goal`
18. `KPIs / Manager Get KPIs By Goal`
19. `KPIs / Manager Get KPIs By User`
20. `Evaluations / Run Evaluation`
21. `Evaluations / Get Evaluation By Id`
22. `Evaluations / Download Scorecard PDF`

## Note About `employeeId`

Use `Users / Get Employees` after manager login to fetch employee records, then copy the target employee ids into `employeeId` and `employeeTwoId`.

## Automatically Captured Variables

The collection stores these values automatically:

- `adminToken` after admin login
- `managerToken` after manager one login
- `managerTwoToken` after manager two login
- `employeeToken` after employee one login
- `employeeTwoToken` after employee two login
- `goalId` after goal one creation
- `goalTwoId` after goal two creation
- `evaluationId` after evaluation creation

## First-Time Postman Usage

1. Import both JSON files into Postman.
2. Open the environment selector in the top-right corner.
3. Choose `PEC Local`.
4. Start with the `Auth` folder and click `Send` on each request in order.
5. After login requests, Postman automatically stores tokens in the environment.
6. Run `Users / Get Employees`, copy employee ids, and paste them into `employeeId` and `employeeTwoId`.
7. Continue with `Goals`, `Rules`, `KPIs`, and `Evaluations`.

## Editing Environment Variables

In Postman:

1. Click the eye icon in the top-right.
2. Open the active environment.
3. Update values like `employeeId`, `employeeTwoId`, `goalId`, `goalTwoId`, or `evaluationId`.
4. Save the environment.
