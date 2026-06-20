package com.sum1t.preppy.common.utils.endpoints

sealed class GradesEndPoint(route: String) {
    val route = "grades/${route}"
    object AllGrades: GradesEndPoint("all")
}

sealed class SubjectsEndPoint(route: String) {
    val route = "subjects/${route}"
    object AllSubjects: SubjectsEndPoint("all")
}

sealed class QuestionsEndPoint(route: String) {
    val route = "questions/${route}"
    object AllQuestionsByTopicIds: QuestionsEndPoint("filter")
}