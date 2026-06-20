package com.example.TestPrep.DTO.request

import com.example.TestPrep.DTO.question.QuestionDTO

// Root request object
data class BulkQuestionRequest(
    val questions: List<QuestionDTO>
)













//curl --location 'http://localhost:8080/api/questions/bulk' \
//--header 'Content-Type: application/json' \
//--data '{
//"questions": [
//
//{
//    "grade": { "name": "Class 12" },
//    "subject": { "name": "Physics" },
//    "topic": { "name": "Electrostatics" },
//    "questionText": "What is Coulomb’s law?",
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "explanation": "Force between two charges is proportional to product of charges and inversely proportional to square of distance",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "F = k q1 q2 / r²", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "F = ma", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 12" },
//    "subject": { "name": "Physics" },
//    "topic": { "name": "Current Electricity" },
//    "questionText": "Ohm’s law states?",
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "explanation": "V = IR",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "V = IR", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "P = VI", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 12" },
//    "subject": { "name": "Math" },
//    "topic": { "name": "Calculus" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Derivative of x^3?",
//    "explanation": "Power rule",
//    "difficultyLevel": "Medium",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "3x^2", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "x^2", "isCorrect": false, "optionOrder": 2 },
//    { "optionText": "2x^3", "isCorrect": false, "optionOrder": 3 },
//    { "optionText": "3x^2", "isCorrect": false, "optionOrder": 4 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 11" },
//    "subject": { "name": "Physics" },
//    "topic": { "name": "Laws of Motion" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Newton’s first law is also called?",
//    "explanation": "Law of inertia",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "Law of inertia", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "F = ma", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 11" },
//    "subject": { "name": "Chemistry" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "topic": { "name": "Atomic Structure" },
//    "questionText": "Who discovered electron?",
//    "explanation": "J.J. Thomson",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "J.J. Thomson", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "Rutherford", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 11" },
//    "subject": { "name": "Chemistry" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "topic": { "name": "Chemical Bonding" },
//    "questionText": "Ionic bond is formed by?",
//    "explanation": "Transfer of electrons",
//    "difficultyLevel": "Medium",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "Transfer of electrons", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "Sharing electrons", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 10" },
//    "subject": { "name": "Math" },
//    "topic": { "name": "Algebra" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Solve: x + 5 = 10",
//    "explanation": "x = 5",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "5", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "10", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//{
//    "grade": { "name": "Class 10" },
//    "subject": { "name": "Math" },
//    "topic": { "name": "Algebra" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Solve: x - 6 = 10",
//    "explanation": "Subtraction on both sides",
//    "difficultyLevel": "Easy",
//    "questionType": "INTEGER",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "16", "isCorrect": true, "optionOrder": 1 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 10" },
//    "subject": { "name": "Physics" },
//    "topic": { "name": "Motion" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Unit of velocity?",
//    "explanation": "m/s",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "m/s", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "kg", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 9" },
//    "subject": { "name": "Physics" },
//    "topic": { "name": "Force" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "SI unit of force?",
//    "explanation": "Newton",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "Newton", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "Joule", "isCorrect": false, "optionOrder": 2 }
//    ]
//},
//
//{
//    "grade": { "name": "Class 9" },
//    "subject": { "name": "Biology" },
//    "topic": { "name": "Cell" },
//    "marks": 4,
//    "negativeMarks": 0.25,
//    "questionText": "Powerhouse of cell?",
//    "explanation": "Mitochondria",
//    "difficultyLevel": "Easy",
//    "questionType": "SINGLE",
//    "questionTier": "FREE",
//    "options": [
//    { "optionText": "Mitochondria", "isCorrect": true, "optionOrder": 1 },
//    { "optionText": "Nucleus", "isCorrect": false, "optionOrder": 2 },
//    { "optionText": "Vaculoe", "isCorrect": false, "optionOrder": 3 },
//    { "optionText": "Chlorophyll", "isCorrect": false, "optionOrder": 4 }
//    ]
//}
//
//]
//}'