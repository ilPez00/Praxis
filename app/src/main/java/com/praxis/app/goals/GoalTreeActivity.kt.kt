// GoalTreeActivity.kt
package com.example.praxis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.View

class `GoalTreeActivity.kt` : AppCompatActivity() {

    private lateinit var rootGoalInput: EditText
    private lateinit var addChildButton: Button
    private lateinit var childrenContainer: LinearLayout
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_tree)

        rootGoalInput = findViewById(R.id.input_root_goal)
        addChildButton = findViewById(R.id.btn_add_child)
        childrenContainer = findViewById(R.id.container_children)
        saveButton = findViewById(R.id.btn_save)

        addChildButton.setOnClickListener {
            addChildGoalInput()
        }

        saveButton.setOnClickListener {
            saveGoalTree()
        }
    }

    private fun addChildGoalInput() {
        val inflater = LayoutInflater.from(this)
        val childView: View = inflater.inflate(R.layout.item_child_goal, childrenContainer, false)
        childrenContainer.addView(childView)
    }

    private fun saveGoalTree() {
        val rootGoal = rootGoalInput.text.toString().trim()
        if (rootGoal.isEmpty()) {
            rootGoalInput.error = "Enter root goal"
            return
        }
        val childGoals = mutableListOf<String>()
        for (i in 0 until childrenContainer.childCount) {
            val item = childrenContainer.getChildAt(i)
            val childInput = item.findViewById<EditText>(R.id.input_child_goal)
            val text = childInput?.text.toString().trim()
            if (text.isNotEmpty()) childGoals.add(text)
        }

        // TODO: save or process the goal tree
        // e.g., persist to database, send to server, or pass to next screen
    }
}
