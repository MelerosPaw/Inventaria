package meleros.paw.inventory.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.QuantityViewBinding
import meleros.paw.inventory.extension.hideKeyboard
import meleros.paw.inventory.extension.isTrue

class QuantityView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

  private val binding: QuantityViewBinding
  var amount: Int?
    get() = binding.inputAmount.text.toValidQuantity()
    set(value) {
      if (value in minAmount.. maxAmount) {
        binding.inputAmount.setText(value.toString())
        onAmountChanged()
      }
    }

  var minAmount: Int = Int.MIN_VALUE
    set(value) {
      field = value
      onAmountChanged()
    }
  var maxAmount: Int = Int.MAX_VALUE
    set(value) {
      field = value
      onAmountChanged()
    }

  init {
    binding = QuantityViewBinding.inflate(LayoutInflater.from(context), this)
    setUpButtons()
    setUpTextField()
  }

  private fun setUpButtons() {
    binding.btnDecrease.setOnClickListener {
      if (amountExceedsUpperLimit()) {
        amount = maxAmount
      } else if (amount?.canBeDecreased().isTrue()) {
        amount = amount?.dec()
      }

      removeFocusFromTextView()
      hideKeyboard()
      removeError()
    }

    binding.btnIncrease.setOnClickListener {
      amount
        ?.takeIf { it.canBeIncreased() }
        ?.let { amount = it.inc() }
      removeFocusFromTextView()
      hideKeyboard()
      removeError()
    }
  }

  private fun setUpTextField() {
    binding.inputAmount.doAfterTextChanged { text ->
      val currentAmount = amount
      when {
        text.isNullOrBlank() || currentAmount == null -> setAmountOutOfBoundsError()
        currentAmount > maxAmount -> amount = maxAmount
        currentAmount < minAmount -> amount = minAmount
        else -> onAmountChanged()
      }
    }

    binding.inputAmount.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
      with(binding.inputAmount) {
        if (!hasFocus && text.isNotAValidValue()) {
          amount = minAmount
          hideKeyboard()
          removeError()
        }
      }
    }
  }

  private fun setAmountOutOfBoundsError() {
    val error = "La cantidad debe estar entre $minAmount y $maxAmount"
    binding.inputAmount.error = error
    onAmountChanged()
  }

  private fun removeError() {
    binding.inputAmount.error = null
  }

  private fun Editable?.isNotAValidValue() = isNullOrBlank() || amount.exceedsLimits()

  private fun CharSequence.toValidQuantity(): Int? = toString().toIntOrNull()

  private fun onAmountChanged() {
    with(binding) {
      val decreaseButtonEnabled = amount?.canBeDecreased().isTrue() || amountExceedsUpperLimit()
      val increaseButtonEnabled = amount?.canBeIncreased().isTrue() || inputAmount.text.isBlank()

      btnDecrease.isEnabled = decreaseButtonEnabled
      btnIncrease.isEnabled = increaseButtonEnabled
      btnDecrease.backgroundTintList = getButtonBackgroundColor(decreaseButtonEnabled)
      btnIncrease.backgroundTintList = getButtonBackgroundColor(increaseButtonEnabled)
      btnDecrease.imageTintList = getButtonIconColor(decreaseButtonEnabled)
      btnIncrease.imageTintList = getButtonIconColor(increaseButtonEnabled)
    }
  }

  private fun amountExceedsUpperLimit() = amount == null && binding.inputAmount.text.isNotBlank()

  private fun Int?.exceedsLimits(): Boolean = this == null || this !in minAmount..maxAmount

  private fun Int.canBeIncreased(): Boolean = this < maxAmount

  private fun Int.canBeDecreased(): Boolean = this > minAmount

  private fun removeFocusFromTextView() {
    binding.inputAmount.clearFocus()
  }

  private fun getButtonBackgroundColor(isEnabled: Boolean): ColorStateList {
    val enabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primaryColor))
    val disabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
    return enabledColor.takeIf { isEnabled } ?: disabledColor
  }

  private fun getButtonIconColor(isEnabled: Boolean): ColorStateList {
    val enabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
    val disabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
    return enabledColor.takeIf { isEnabled } ?: disabledColor
  }
}