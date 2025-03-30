package kf.sources

import kf.ForthVM

/** Input source for tests: the content can be added to.
 *
 * Since the tests often feed multiple lines in at different points in a test,
 * the "input" here is a string that can be reset from the user.
 */

class SourceTestEvalInput(
    vm: ForthVM, override var content: String
) : SourceBaseString(vm, 0, "<fake>")
