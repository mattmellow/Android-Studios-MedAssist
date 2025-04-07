package com.example.medassist.ui.medication;
import com.example.medassist.ui.reminders.ReminderFormDialog;
import android.app.AlertDialog;
import android.content.Context;

/**
 * Helper class to manage dialog operations
 */
public class DialogHelper {

    /**
     * Show a confirmation dialog for medication deletion
     *
     * @param context The context
     * @param medication The medication to delete
     * @param onConfirm Callback to execute when confirmed
     */
    public static void showDeleteConfirmationDialog(Context context, Medication medication, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Medication")
                .setMessage("Are you sure you want to delete " + medication.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Show the add medication dialog
     *
     * @param fragment The parent fragment
     * @param onMedicationAdded Callback for when a medication is added
     */
    public static void showAddMedicationDialog(MedicationFragment fragment, ReminderFormDialog.OnReminderAddedListener onMedicationAdded) {
        MedicationFormDialog dialog = new MedicationFormDialog();
        dialog.setOnReminderAddedListener(onMedicationAdded); // Update listener interface
        dialog.show(fragment.getChildFragmentManager(), "AddMedicationDialog");
    }

    /**
     * Show the edit medication dialog
     *
     * @param fragment The parent fragment
     * @param medication The medication to edit
     * @param onMedicationEdited Callback for when a medication is edited
     */
    public static void showEditMedicationDialog(MedicationFragment fragment, Medication medication, ReminderFormDialog.OnReminderAddedListener onMedicationEdited) {
        MedicationFormDialog dialog = new MedicationFormDialog();
        dialog.setMedication(medication); // Implement this method in MedicationFormDialog
        dialog.setOnReminderAddedListener(onMedicationEdited); // Update listener interface
        dialog.show(fragment.getChildFragmentManager(), "EditMedicationDialog");
    }
}
