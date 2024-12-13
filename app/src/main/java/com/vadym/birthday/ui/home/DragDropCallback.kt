package com.vadym.birthday.ui.home

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropCallback (private val adapter: PersonAdapter, private val viewModel: MainViewModel) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Allow drag-and-drop movement (up and down).
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // Notify the adapter of the move
        val mutableListPerson = adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
//        viewModel.updatePosition(mutableListPerson)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Swipe functionality not needed here
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true // Enable drag on long press
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
//        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
//            (viewHolder?.itemView?.context as? MainActivity)?.saveUpdatedOrderToFirebase(adapter.getCurrentList())
//        }
    }
}