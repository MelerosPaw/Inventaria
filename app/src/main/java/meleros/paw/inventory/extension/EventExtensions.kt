package meleros.paw.inventory.extension

import meleros.paw.inventory.ui.Event

fun Event<Boolean>.whenTrue(handle: Boolean = true, block: () -> Unit) {
  if (handle && isTrue() || !handle && getUnhandled().isTrue()) {
    block()
  }
}

fun Event<Boolean>.isTrue(): Boolean = get().isTrue()