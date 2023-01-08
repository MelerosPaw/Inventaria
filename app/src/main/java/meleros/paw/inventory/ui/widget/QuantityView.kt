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
      if (amount in minAmount.. maxAmount) { binding.inputAmount.setText(value.toString()) }
    }

  var minAmount: Int = Int.MIN_VALUE
  var maxAmount: Int = Int.MAX_VALUE

  init {
    binding = QuantityViewBinding.inflate(LayoutInflater.from(context), this)
    setUpButtons()
    setUpTextField()
  }

  private fun setUpButtons() {
    binding.btnDecrease.setOnClickListener { if (amount > minAmount) { amount-- } }
    binding.btnIncrease.setOnClickListener { if (amount < maxAmount) { amount++ } }
  }

  private fun setUpTextField() {
    binding.inputAmount.doAfterTextChanged {
      val error: String? = "La cantidad debe ser al menos 1".takeIf { amount == 0 }
      binding.inputAmount.error = error
    }
  }
}