package com.example.tp1calculatrice

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editNombre1: EditText
    private lateinit var editNombre2: EditText
    private lateinit var txtOperateur: TextView
    private lateinit var txtResultat: TextView
    private var operation: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Étape 3 : références des composants
        editNombre1 = findViewById(R.id.editNombre1)
        editNombre2 = findViewById(R.id.editNombre2)
        txtOperateur = findViewById(R.id.txtOperateur)
        txtResultat = findViewById(R.id.txtResultat)
        val grid = findViewById<GridLayout>(R.id.gridTouches)

        // Le clavier du téléphone est remplacé par notre pavé
        editNombre1.showSoftInputOnFocus = false
        editNombre2.showSoftInputOnFocus = false
        editNombre1.requestFocus()

        // Étape 4 : création des boutons et écouteurs de clic
        val touches = listOf(
            "C", "⌫", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ".", "="
        )
        val marge = (6 * resources.displayMetrics.density).toInt()

        for (t in touches) {
            val b = Button(this)
            b.text = t
            b.textSize = 26f
            b.isAllCaps = false
            b.setTextColor(Color.WHITE)
            val fond = android.graphics.drawable.GradientDrawable()
            fond.shape = android.graphics.drawable.GradientDrawable.OVAL
            fond.setColor(
                Color.parseColor(
                    when (t) {
                        "÷", "×", "-", "+", "%" -> "#388E2A"
                        "=" -> "#3478F6"
                        else -> "#1A1A1A"
                    }
                )
            )
            b.background = fond
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            )
            params.width = 0
            params.height = (72 * resources.displayMetrics.density).toInt()
            params.setMargins(marge, marge, marge, marge)
            b.layoutParams = params
            b.setOnClickListener { toucheAppuyee(t) }
            grid.addView(b)
        }
    }

    private fun champActif(): EditText = (currentFocus as? EditText) ?: editNombre1

    private fun toucheAppuyee(t: String) {
        when (t) {
            "C" -> {
                editNombre1.text.clear()
                editNombre2.text.clear()
                txtOperateur.text = "?"
                txtResultat.text = ""
                operation = null
                editNombre1.requestFocus()
            }
            "⌫" -> {
                val c = champActif()
                if (c.text.isNotEmpty()) c.text.delete(c.text.length - 1, c.text.length)
            }
            "±" -> {
                val c = champActif()
                val s = c.text.toString()
                c.setText(if (s.startsWith("-")) s.substring(1) else "-$s")
                c.setSelection(c.text.length)
            }
            "÷" -> choisirOperation('/', "÷")
            "×" -> choisirOperation('*', "×")
            "-" -> choisirOperation('-', "-")
            "+" -> choisirOperation('+', "+")
            "%" -> choisirOperation('%', "%")
            "=" -> calculer()
            else -> champActif().append(t)
        }
    }

    private fun choisirOperation(op: Char, symbole: String) {
        operation = op
        txtOperateur.text = symbole
        editNombre2.requestFocus()
    }

    // Étapes 5 et 6 : calcul et division par zéro
    private fun calculer() {
        val a = editNombre1.text.toString().toDoubleOrNull()
        val b = editNombre2.text.toString().toDoubleOrNull()
        val op = operation

        if (a == null || b == null) {
            Toast.makeText(this, "Saisissez les deux nombres", Toast.LENGTH_SHORT).show()
            return
        }
        if (op == null) {
            Toast.makeText(this, "Choisissez une opération", Toast.LENGTH_SHORT).show()
            return
        }
        if ((op == '/' || op == '%') && b == 0.0) {
            Toast.makeText(this, "Division par zéro impossible", Toast.LENGTH_SHORT).show()
            return
        }

        val resultat = when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '%' -> a % b
            else -> return
        }

        txtResultat.text =
            if (resultat % 1.0 == 0.0) resultat.toLong().toString() else resultat.toString()
    }
}