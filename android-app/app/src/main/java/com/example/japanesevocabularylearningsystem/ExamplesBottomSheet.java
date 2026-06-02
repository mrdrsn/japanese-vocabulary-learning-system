package com.example.japanesevocabularylearningsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.ExampleRowAdapter;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ExamplesBottomSheet extends BottomSheetDialogFragment {

    public static final String RESULT_LU_CLICKED      = "lu_clicked";
    public static final String RESULT_SHEET_DISMISSED = "examples_dismissed";
    public static final String KEY_LU_ID              = "luId";
    public static final String KEY_LU_ROMAJI          = "luRomaji";

    private static final String ARG_PATTERN  = "pattern";
    private static final String ARG_EXAMPLES = "examples";

    public static ExamplesBottomSheet newInstance(String pattern,
                                                  List<Utterance.ExampleEntry> examples) {
        ExamplesBottomSheet sheet = new ExamplesBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PATTERN, pattern);
        args.putSerializable(ARG_EXAMPLES, new ArrayList<>(examples));
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_examples_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String pattern = args != null ? args.getString(ARG_PATTERN, "") : "";
        @SuppressWarnings("unchecked")
        ArrayList<Utterance.ExampleEntry> examples = args != null
                ? (ArrayList<Utterance.ExampleEntry>) args.getSerializable(ARG_EXAMPLES)
                : new ArrayList<>();
        if (examples == null) examples = new ArrayList<>();

        view.<TextView>findViewById(R.id.tvExamplesPattern).setText(pattern);
        view.findViewById(R.id.btnCloseExamples).setOnClickListener(v -> dismiss());

        RecyclerView rv = view.findViewById(R.id.rvExamples);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        final ArrayList<Utterance.ExampleEntry> finalExamples = examples;
        rv.setAdapter(new ExampleRowAdapter(finalExamples, (luId, luRomaji) -> {
            Bundle result = new Bundle();
            result.putString(KEY_LU_ID, luId);
            result.putString(KEY_LU_ROMAJI, luRomaji);
            getParentFragmentManager().setFragmentResult(RESULT_LU_CLICKED, result);
            dismiss();
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getParentFragmentManager().setFragmentResult(RESULT_SHEET_DISMISSED, new Bundle());
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }
}