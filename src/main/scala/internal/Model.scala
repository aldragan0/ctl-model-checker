package internal

/*

 */
class Model(
    val states: Set[String],
    val transitions: Set[(String, String)],
    val stateLabels: Map[String, Set[String]],
    val initialState: String
)
