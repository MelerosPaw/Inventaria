package meleros.paw.inventory.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import meleros.paw.inventory.databinding.QuantityViewBinding

class QuantityView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

  private val binding: QuantityViewBinding
  var amount: Int
    get() = binding.inputAmount.text.toString().toIntOrNull() ?: 0
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
      if (amount > minAmount) {
        amount--
      }
    }
    binding.btnIncrease.setOnClickListener {
      if (amount < maxAmount) {
        amount++
      }
    }
  }

  private fun setUpTextField() {
    binding.inputAmount.doAfterTextChanged {
      val error: String? = "La cantidad debe ser al menos $minAmount".takeIf { amount < minAmount }
      binding.inputAmount.error = error
      onAmountChanged()
    }
  }

  private fun onAmountChanged() {
    binding.btnDecrease.isEnabled = amount > minAmount
    binding.btnIncrease.isEnabled = amount < maxAmount
  }
}