package com.example.medassist.ui.medication;
import com.example.medassist.ui.reminders.ReminderFormDialog;
import android.app.AlertDialog;
import android.content.Context;

public class DialogHelper {
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

    public static void showAddMedicationDialog(MedicationFragment fragment, ReminderFormDialog.OnReminderAddedListener onMedicationAdded) {
        MedicationFormDialog dialog = new MedicationFormDialog();
        dialog.setOnReminderAddedListener(onMedicationAdded);
        dialog.show(fragment.getChildFragmentManager(), "AddMedicationDialog");
    }

    public static void showEditMedicationDialog(MedicationFragment fragment, Medication medication, ReminderFormDialog.OnReminderAddedListener onMedicationEdited) {
        MedicationFormDialog dialog = new MedicationFormDialog();
        dialog.setMedication(medication);
        dialog.setOnReminderAddedListener(onMedicationEdited);
        dialog.show(fragment.getChildFragmentManager(), "EditMedicationDialog");
    }
}
