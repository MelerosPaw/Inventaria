package meleros.paw.inventory.ui

class Event<T>(private val value: T) {

  private var handled: Boolean = false

  fun get(): T? = value.takeUnless { handled }?.also { handled = true }

  fun getUnhandled(): T? = value
}