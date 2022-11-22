package com.example.splitthebill.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.splitthebill.R
import com.example.splitthebill.adapter.ItemAdapter
import com.example.splitthebill.databinding.ActivityParticipationBinding
import com.example.splitthebill.model.Item
import com.example.splitthebill.model.Model.EXTRA_ITEM
import com.example.splitthebill.model.Model.EXTRA_PARTICIPATION
import com.example.splitthebill.model.Model.VIEW_ITEM
import com.example.splitthebill.model.Model.VIEW_PARTICIPATION
import com.example.splitthebill.model.Participation
import kotlin.math.log
import kotlin.random.Random

class ParticipationActivity : AppCompatActivity() {

    private val binding: ActivityParticipationBinding by lazy {
        ActivityParticipationBinding.inflate(layoutInflater)
    }

    private lateinit var participation: Participation

    private lateinit var itemAdapter: ItemAdapter

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val item = result.data?.getParcelableExtra<Item>(EXTRA_ITEM)

                item?.let { i ->
                    val pos = participation.items.indexOfFirst { it.id == i.id }

                    if (pos == -1) {
                        participation.items.add(i)
                    } else {
                        participation.items[pos] = i
                    }

                    itemAdapter.notifyDataSetChanged()
                }
            }
        }

        registerForContextMenu(binding.itemsLv)

        binding.itemsLv.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = participation.items[position]
                val itemIntent = Intent(this@ParticipationActivity, ItemActivity::class.java)
                itemIntent.putExtra(EXTRA_ITEM, item)
                itemIntent.putExtra(VIEW_ITEM, true)
                startActivity(itemIntent)
            }
        }

        val receivedParticipation = intent.getParcelableExtra<Participation>(EXTRA_PARTICIPATION)
        val viewParticipation = intent.getBooleanExtra(VIEW_PARTICIPATION, false)
        val ctx = this;

        receivedParticipation?.let { part -> with(binding) {
            participation = part
        }} ?: run {
            participation = Participation(
                Random(System.currentTimeMillis()).nextInt(),
                "",
                0L,
                mutableListOf(),
            )
        }

        itemAdapter = ItemAdapter(ctx, participation.items)

        binding.nameEt.setText(participation.name)
        binding.itemsLv.adapter = itemAdapter

        if (viewParticipation) {
            binding.nameEt.isEnabled = false
            binding.saveBt.visibility = View.GONE
        }

        binding.saveBt.setOnClickListener {
            val value = participation?.items?.map { i -> i.value }?.fold(0L) { s, e -> s + e } ?: 0

            val participation = Participation(
                id    = participation?.id ?: Random(System.currentTimeMillis()).nextInt(),
                name  = binding.nameEt.text.toString(),
                value = value,
                items = participation?.items ?: mutableListOf(),
            )

            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_PARTICIPATION, participation)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addItemMi -> {
                carl.launch(Intent(this, ItemActivity::class.java))
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.item_ctx_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position

        return when (item.itemId) {
            R.id.removeItemMi -> {
                participation?.let { part -> with(binding) {
                    part.items.removeAt(position)
                    itemAdapter.notifyDataSetChanged()
                }}

                true
            }
            R.id.editItemMi -> {
                val ctx = this

                participation?.let { part -> with(binding) {
                    val itemValue = part.items[position]
                    val itemIntent = Intent(ctx, ItemActivity::class.java)
                    itemIntent.putExtra(EXTRA_ITEM, itemValue)
                    itemIntent.putExtra(VIEW_ITEM, false)
                    carl.launch(itemIntent)
                }}

                true
            }
            else -> {
                false
            }
        }
    }
}