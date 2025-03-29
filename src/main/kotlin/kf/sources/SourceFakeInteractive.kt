package kf.sources

import kf.ForthVM

/** Input source for tests: the content can be added to.
 *
 * Since the tests often feed multiple lines in at different points in a test,
 * the "input" here is a string that can be reset from the user.
 */

class SourceFakeInteractive(
    vm: ForthVM, override var content: String
) : SourceString(vm, 0, "<fake>")
