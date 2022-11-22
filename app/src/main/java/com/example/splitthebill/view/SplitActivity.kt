package com.example.splitthebill.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.splitthebill.R
import com.example.splitthebill.adapter.ParticipationAdapter
import com.example.splitthebill.databinding.ActivitySplitBinding
import com.example.splitthebill.model.Item
import com.example.splitthebill.model.Model.EXTRA_PARTICIPATION
import com.example.splitthebill.model.Model.EXTRA_TRANSACTION_LIST
import com.example.splitthebill.model.Model.VIEW_PARTICIPATION
import com.example.splitthebill.model.Participation
import com.example.splitthebill.model.Transaction
import com.example.splitthebill.model.TransactionList

class SplitActivity: AppCompatActivity() {

    private val binding: ActivitySplitBinding by lazy {
        ActivitySplitBinding.inflate(layoutInflater)
    }

    private val participationList: MutableList<Participation> = mutableListOf()

    private lateinit var participationAdapter: ParticipationAdapter

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // mockParticipationList()

        participationAdapter = ParticipationAdapter(this, participationList)
        binding.participantsLv.adapter = participationAdapter

        val splitFab: View = findViewById(R.id.splitFab)
        splitFab.setOnClickListener {
            calculateSplitAndShow()
        }

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val participation = result.data?.getParcelableExtra<Participation>(EXTRA_PARTICIPATION)

                participation?.let { part ->
                    val pos = participationList.indexOfFirst { it.id == part.id }

                    if (pos == -1) {
                        participationList.add(part)
                    } else {
                        participationList[pos] = part
                    }

                    participationAdapter.notifyDataSetChanged()
                }
            }
        }

        registerForContextMenu(binding.participantsLv)

        binding.participantsLv.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val participation = participationList[position]
                val participationIntent = Intent(this@SplitActivity, ParticipationActivity::class.java)
                participationIntent.putExtra(EXTRA_PARTICIPATION, participation)
                participationIntent.putExtra(VIEW_PARTICIPATION, true)
                startActivity(participationIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.participation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addParticipationMi -> {
                carl.launch(Intent(this, ParticipationActivity::class.java))
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.participation_ctx_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when (item.itemId) {
            R.id.removeParticipationMi -> {
                participationList.removeAt(position)
                participationAdapter.notifyDataSetChanged()
                true
            }
            R.id.editParticipationMi -> {
                val participation = participationList[position]
                val participationIntent = Intent(this, ParticipationActivity::class.java)
                participationIntent.putExtra(EXTRA_PARTICIPATION, participation)
                participationIntent.putExtra(VIEW_PARTICIPATION, false)
                carl.launch(participationIntent)
                true
            }
            else -> {
                false
            }
        }
    }

    private fun calculateSplitAndShow() {
        // Calculate
        val nParticipants = participationList.size

        if (nParticipants > 0) {
            val totalValue = participationList
                .map { part -> part.value }
                .fold(0L) { s, e -> s + e }
            val valuePerParticipant = totalValue/nParticipants
            val tl = participationList
                .withIndex()
                .map { (i, part) ->
                    // Negativo pq se o cara deu mais do q ele precisava, a conta fica negativa
                    var value = -(valuePerParticipant - part.value)
                    var owing = false

                    if (value < 0) {
                        value *= -1
                        owing = true
                    }

                    Transaction(i, part.name, value, owing)
                }

            val transactionList = TransactionList(tl)

            // Show
            val intent = Intent(this@SplitActivity, SplittedBillActivity::class.java)
            intent.putExtra(EXTRA_TRANSACTION_LIST, transactionList)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Adicione participantes antes de dividir", Toast.LENGTH_LONG).show()
        }
    }

    private fun mockParticipationList() {
        for (i in 1..6) {
            val items: MutableList<Item> = mutableListOf()
            items.add(Item(0, "Banana", 100L * i))
            items.add(Item(1, "Sugar", 300))

            participationList.add(
                Participation(
                    id    = i,
                    name  = "Alice",
                    value = 100L * i + 300,
                    items = items,
                )
            )
        }
    }
}