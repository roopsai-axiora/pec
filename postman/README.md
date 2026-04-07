# Postman Setup

This folder contains a fuller Postman setup for exercising the PEC API with a realistic org structure.

Scenario:
- `1` admin
- `2` managers
- `Manager One` owns `3` employees
- `Manager Two` owns `2` employees
- `Manager One` assigns `3` goals
- `Manager Two` assigns `4` goals across `2` employees
- `Admin` defines the active evaluation rules used across the organization
- all KPI submissions are performed by the respective employees
- evaluations are run by the responsible manager

Business context:
- `{{orgName}}`
- `{{businessContext}}`
- period: `{{period}}`

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
- `businessContext`
- `period`
- `defaultPassword`
- `adminToken`
- `managerOneToken`
- `managerTwoToken`
- `employeeOneToken`
- `employeeTwoToken`
- `employeeThreeToken`
- `employeeFourToken`
- `employeeFiveToken`
- `managerOneId`
- `managerTwoId`
- `employeeOneId`
- `employeeTwoId`
- `employeeThreeId`
- `employeeFourId`
- `employeeFiveId`
- `goalOneId` through `goalSevenId`
- `evaluationOneId` through `evaluationFiveId`

## Recommended Run Order

1. `Auth / Register Bootstrap Admin`
2. `Auth / Login Admin`
3. `Auth / Register Manager One`
4. `Auth / Register Manager Two`
5. `Auth / Resolve Manager IDs From Audit`
6. `Auth / Register Manager One Employee One`
7. `Auth / Register Manager One Employee Two`
8. `Auth / Register Manager One Employee Three`
9. `Auth / Register Manager Two Employee One`
10. `Auth / Register Manager Two Employee Two`
11. `Auth / Login Manager One`
12. `Auth / Login Manager Two`
13. `Auth / Login Employee One`
14. `Auth / Login Employee Two`
15. `Auth / Login Employee Three`
16. `Auth / Login Employee Four`
17. `Auth / Login Employee Five`
18. `Users / Manager One Get Team`
19. `Users / Manager Two Get Team`
20. `Goals / M1 Create Goal For Employee One`
21. `Goals / M1 Create Goal For Employee Two`
22. `Goals / M1 Create Goal For Employee Three`
23. `Goals / M2 Create Goal Four For Employee Four`
24. `Goals / M2 Create Goal Five For Employee Four`
25. `Goals / M2 Create Goal Six For Employee Five`
26. `Goals / M2 Create Goal Seven For Employee Five`
27. `Rules / Create High Achiever Rule` using `adminToken`
28. `Rules / Create Consistent Performer Rule` using `adminToken`
29. `Rules / Create At Risk Rule` using `adminToken`
30. `Rules / Get Active Rules` using `adminToken`
31. `KPIs / Employee One Submit KPI`
32. `KPIs / Employee Two Submit KPI`
33. `KPIs / Employee Three Submit KPI`
34. `KPIs / Employee Four Submit KPI For Goal Four`
35. `KPIs / Employee Four Submit KPI For Goal Five`
36. `KPIs / Employee Five Submit KPI For Goal Six`
37. `KPIs / Employee Five Submit KPI For Goal Seven`
38. `KPIs / Manager One Get KPIs By User`
39. `KPIs / Manager Two Get KPIs By Goal`
40. `Evaluations / Run Evaluation For Employee One`
41. `Evaluations / Run Evaluation For Employee Two`
42. `Evaluations / Run Evaluation For Employee Three`
43. `Evaluations / Run Evaluation For Employee Four`
44. `Evaluations / Run Evaluation For Employee Five`
45. `Evaluations / Employee One Get Own Evaluations`
46. `Evaluations / Manager Two Download Employee Four Scorecard`
47. `Audit / Admin Get Audit By Action`

## How IDs Are Resolved

Two requests populate IDs automatically:

- `Auth / Resolve Manager IDs From Audit`
  - reads `USER_REGISTERED` audit logs
  - extracts `managerOneId` and `managerTwoId`

- `Users / Manager One Get Team`
  - sets `employeeOneId`, `employeeTwoId`, `employeeThreeId`

- `Users / Manager Two Get Team`
  - sets `employeeFourId`, `employeeFiveId`

Goal and evaluation IDs are automatically captured from their respective create/run responses.

## Automatically Captured Variables

- `adminToken`
- `managerOneToken`
- `managerTwoToken`
- `employeeOneToken`
- `employeeTwoToken`
- `employeeThreeToken`
- `employeeFourToken`
- `employeeFiveToken`
- `managerOneId`
- `managerTwoId`
- `employeeOneId`
- `employeeTwoId`
- `employeeThreeId`
- `employeeFourId`
- `employeeFiveId`
- `goalOneId` through `goalSevenId`
- `evaluationOneId` through `evaluationFiveId`

## First-Time Postman Usage

1. Import both JSON files into Postman.
2. Select the `PEC Local` environment.
3. Run the requests in the order shown above.
4. Do not skip the two ID-resolution steps:
   - `Resolve Manager IDs From Audit`
   - `Manager One Get Team` and `Manager Two Get Team`
5. After those steps, the remaining requests should use environment variables automatically.

## Notes

- This collection assumes a clean or controlled local dataset.
- The first admin registration is intentionally the bootstrap account.
- All later account creation is done by admin using the current API rules.
- All rule creation and rule lookup is now performed by admin.
- Manager one owns the application delivery team.
- Manager two owns the customer support team.
