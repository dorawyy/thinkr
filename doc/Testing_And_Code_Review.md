# Example M5: Testing and Code Review

## 1. Change History

| **Change Date**   | **Modified Sections** | **Rationale** |
| ----------------- | --------------------- | ------------- |
| _Nothing to show_ |

---

## 2. Back-end Test Specification: APIs

### 2.1. Locations of Back-end Tests and Instructions to Run Them

#### 2.1.1. Tests

| **Interface**                 | **Describe Group Location, No Mocks**                | **Describe Group Location, With Mocks**            | **Mocked Components**              |
| ----------------------------- | ---------------------------------------------------- | -------------------------------------------------- | ---------------------------------- |
| **POST /auth/login** | [backend/src/tests/unmocked/userAuthRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/userAuthRoutes.test.ts#L73) | [backend/src/tests/mocked/userAuthLogin.test](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/userAuthLogin.test.ts#L49) | MongoDB |
| **GET /chat** | [backend/src/tests/unmocked/chatRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/chatRoutes.test.ts#L79) | [backend/src/tests/mocked/chatRoutes.test](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/chatRoutes.test.ts#L40) | MongoDB, LangChain OpenAI |
| **POST /chat/message** | [backend/src/tests/unmocked/chatRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/chatRoutes.test.ts#L221) | [backend/src/tests/mocked/chatRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/chatRoutes.test.ts#L69) | MongoDB, LangChain OpenAI |
| **DELETE /chat/history** | [backend/src/tests/unmocked/chatRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/chatRoutes.test.ts#L343) | [backend/src/tests/mocked/chatRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/chatRoutes.test.ts#L111) | MongoDB |
| **POST /document/upload** | [backend/src/tests/unmocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/documentRoutes.test.ts#L90) | [backend/src/tests/mocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/documentRoutes.test.ts#L75) | MongoDB, AWS S3, AWS Textract |
| **DELETE /document/delete** | [backend/src/tests/unmocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/documentRoutes.test.ts#L255) | [backend/src/tests/mocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/documentRoutes.test.ts#L119) | MongoDB, AWS S3 |
| **GET /document/retrieve** | [backend/src/tests/unmocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/documentRoutes.test.ts#L362) | [backend/src/tests/mocked/documentRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/documentRoutes.test.ts#L156) | MongoDB |
| **GET /study/quiz** | [backend/src/tests/unmocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/studyRoutes.test.ts#L165) | [backend/src/tests/mocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/studyRoutes.test.ts#L67) | MongoDB |
| **GET /study/flashcards** | [backend/src/tests/unmocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/studyRoutes.test.ts#L87) | [backend/src/tests/mocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/studyRoutes.test.ts#L38) | MongoDB |
| **GET /study/suggestedMaterials** | [backend/src/tests/unmocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/studyRoutes.test.ts#L261) | [backend/src/tests/mocked/studyRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/studyRoutes.test.ts#L94) | MongoDB, Vector Database (ChromaDB) |
| **POST /subscription** | [backend/src/tests/unmocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/subscriptionRoutes.test.ts#L43) | [backend/src/tests/mocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/subscriptionRoutes.test.ts#L34) | MongoDB |
| **DELETE /subscription** | [backend/src/tests/unmocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/subscriptionRoutes.test.ts#L144) | [backend/src/tests/mocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/subscriptionRoutes.test.ts#L63) | MongoDB |
| **GET /subscription** | [backend/src/tests/unmocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/unmocked/subscriptionRoutes.test.ts#L224) | [backend/src/tests/mocked/subscriptionRoutes.test.ts](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/mocked/subscriptionRoutes.test.ts#L92) | MongoDB |


#### 2.1.2. Commit Hash Where Tests Run

`[Insert Commit SHA here]`

#### 2.1.3. Explanation on How to Run the Tests

1. **Clone the Repository**:

   - Open your terminal and run:
     ```
     git clone https://github.com/jaidensiu/thinkr.git
     ```

2. **Build and Run Backend tests**

   - `cd backend`
   - `npm install`
   - `npm run build`
   - `npm run test` OR `npm run test:coverage` (with code coverage)
   - NOTE: for non-functional tests, you will need an environment variable in a `.env` file in the `backend` directory, please contact the team for this variable when needed

### 2.2. GitHub Actions Configuration Location

`~/.github/workflows/backend-ci.yml` 

`~/.github/workflows/frontend-ci.yml`

### 2.3. Jest Coverage Report Screenshots With Mocks

![Mocked Coverage](./image/mocked-coverage.png)

### 2.4. Jest Coverage Report Screenshots Without Mocks

![Unmocked Coverage](./image/unmocked-coverage.png)

---

## 3. Back-end Test Specification: Tests of Non-Functional Requirements

### 3.1. Test Locations in Git

| **Non-Functional Requirement**  | **Location in Git**                              |
| ------------------------------- | ------------------------------------------------ |
| **Document Similarity Search Performance**          | [`backend\src\tests\nonfunctional\similaritySearchPerformance.test.ts`](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/nonfunctional/similaritySearchPerformance.test.ts) |
| **Document Upload and Study Material Generation Performance** | [`backend\src\tests\nonfunctional\quizGenerationPerformance.test.ts`](https://github.com/jaidensiu/thinkr/blob/master/backend/src/tests/nonfunctional/quizGenerationPerformance.test.ts) |

### 3.2. Test Verification and Logs

- **Document Similarity Search Performance**

  - **Verification:** This test simulates a single API call with Jest to the endpoint that performs a similarity search between different users' documents and returns the study materials that are most similar to what the caller has been studying. The focus is on the time it takes for this similarity search to finish, which we identified as no more than 11.3 seconds based on our design specifications. The test logs capture the response time of the call to the endpoint and the performance margin (difference between response time and threshold time). We then analyze these logs to verify that performance standards are met and don't hinder user experience.

  - **Log Output**
    ```
    PERFORMANCE SUMMARY: Suggested Materials Request
           -----------------------------------------------
           ✧ Response Time:      0.35 seconds
           ✧ Threshold:          11.3 seconds
           ✧ Performance Margin: 10.95 seconds
           ✧ Status:             PASSED ✅
           -----------------------------------------------

      at src/tests/nonfunctional/similaritySearchPerformance.test.ts:29:17

    PASS  src/tests/nonfunctional/similaritySearchPerformance.test.ts (5.14 s)
    ```

- **Document Upload and Study Material Generation Performance**
  - **Verification:** This test simulates a single API call with Jest to the endpoint that uploads a document to the application and polls the document endpoint to verify that quizzes and flashcards were generated. The focus is on the time it takes to upload a document and for quizzes and flashcards to be generated, which we identified as no more than 11.3 seconds based on our design specifications. The test logs capture the response time of the call to the endpoint and the performance margin (difference between response time and threshold time). We then analyze these logs to verify that performance standards are met and don't hinder user experience. **Note that currently, this NFR is not met as we are using a weaker OCR service from AWS Textract due to this project still being in the MVP phase.**
  - **Log Output**
    ```
    PERFORMANCE SUMMARY: Quiz/Flashcard Generation
               -----------------------------------------------
               ✧ Response Time:      17.34 seconds
               ✧ Threshold:          11.3 seconds
               ✧ Performance Margin: -6.04 seconds
               ✧ Status:             FAILED ❌
               -----------------------------------------------

    at src/tests/nonfunctional/quizGenerationPerformance.test.ts:69:21
    ```

---

## 4. Front-end Test Specification

### 4.1. Location in Git of Front-end Test Suite:

`frontend/src/androidTest/java/com/studygroupfinder/`

### 4.2. Tests

- **Use Case: Login**

  - **Expected Behaviors:**
    | **Scenario Steps** | **Test Case Steps** |
    | ------------------ | ------------------- |
    | 1. The user opens â€œAdd Todo Itemsâ€ screen. | Open â€œAdd Todo Itemsâ€ screen. |
    | 2. The app shows an input text field and an â€œAddâ€ button. The add button is disabled. | Check that the text field is present on screen.<br>Check that the button labelled â€œAddâ€ is present on screen.<br>Check that the â€œAddâ€ button is disabled. |
    | 3a. The user inputs an ill-formatted string. | Input â€œ_^_^^OQ#$â€ in the text field. |
    | 3a1. The app displays an error message prompting the user for the expected format. | Check that a dialog is opened with the text: â€œPlease use only alphanumeric charactersâ€. |
    | 3. The user inputs a new item for the list and the add button becomes enabled. | Input â€œbuy milkâ€ in the text field.<br>Check that the button labelled â€œaddâ€ is enabled. |
    | 4. The user presses the â€œAddâ€ button. | Click the button labelled â€œaddâ€. |
    | 5. The screen refreshes and the new item is at the bottom of the todo list. | Check that a text box with the text â€œbuy milkâ€ is present on screen.<br>Input â€œbuy chocolateâ€ in the text field.<br>Click the button labelled â€œaddâ€.<br>Check that two text boxes are present on the screen with â€œbuy milkâ€ on top and â€œbuy chocolateâ€ at the bottom. |
    | 5a. The list exceeds the maximum todo-list size. | Repeat steps 3 to 5 ten times.<br>Check that a dialog is opened with the text: â€œYou have too many items, try completing one firstâ€. |

  - **Test Logs:**
    ```
    [Placeholder for Espresso test execution logs]
    ```

- **Use Case: ...**

  - **Expected Behaviors:**

    | **Scenario Steps** | **Test Case Steps** |
    | ------------------ | ------------------- |
    | ...                | ...                 |

  - **Test Logs:**
    ```
    [Placeholder for Espresso test execution logs]
    ```

- **...**

---

## 5. Automated Code Review Results

### 5.1. Commit Hash Where Codacy Ran

`[Insert Commit SHA here]`

### 5.2. Unfixed Issues per Codacy Category

_(Placeholder for screenshots of Codacyâ€™s Category Breakdown table in Overview)_

### 5.3. Unfixed Issues per Codacy Code Pattern

_(Placeholder for screenshots of Codacyâ€™s Issues page)_

### 5.4. Justifications for Unfixed Issues

- **Code Pattern: [Usage of Deprecated Modules](#)**

  1. **Issue**

     - **Location in Git:** [`src/services/chatService.js#L31`](#)
     - **Justification:** ...

  2. ...

- ...