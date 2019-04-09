package ferox.bracket;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ParticipantsFragment extends Fragment {

    private static final String TAG = "ParticipantsFragment";
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    String url;

    ImageButton participantsOptions;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ArrayList<String> playerSeeds = new ArrayList<>();
    ArrayList<Participant> players = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_participants, container, false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        participantsOptions = v.findViewById(R.id.menu);
        participantsOptions.setOnClickListener(v1 -> {

            PopupMenu popupMenu = new PopupMenu(getContext(), participantsOptions);
            popupMenu.getMenuInflater().inflate(R.menu.participants_fragments_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Add": {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setTitle("Add Participant");
                        View dialogueLayout = getLayoutInflater().inflate(R.layout.fragment_participant_edit_dialog, null);
                        EditText nameText = dialogueLayout.findViewById(R.id.new_participant_name);
                        EditText seedText = dialogueLayout.findViewById(R.id.new_participant_seed);
                        builder.setView(dialogueLayout);
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            Participant player = new Participant();
                            player.setName(nameText.getText().toString());
                            player.setSeed(Integer.parseInt(seedText.getText().toString()));
                            ChallongeRequests.sendRequest(response -> {
                            }, ChallongeRequests.participantCreate(url, player));
                            //TODO this may not refresh consistently try setting the callback to make another challonge request in the event this is not consistent
                            ChallongeRequests.sendRequest(response -> initPlayerList(response), ChallongeRequests.participantIndex(url));


                        })
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                })
                                .create().show();

                        break;
                    }
                    case "Shuffle": {
                        ChallongeRequests.sendRequest(response -> {
                        }, ChallongeRequests.participantRandomize(url));
                        ChallongeRequests.sendRequest(response -> initPlayerList(response), ChallongeRequests.participantIndex(url));
                        break;
                    }
                    case "Refresh": {
                        ChallongeRequests.sendRequest(response -> initPlayerList(response), ChallongeRequests.participantIndex(url));
                        break;
                    }
                }
                return true;
            });
            popupMenu.show();
        });


        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);

        adapter = new RecyclerViewAdapter(getContext(), players, linearLayoutManager, defaultItemAnimator);

        recyclerView = v.findViewById(R.id.participant_list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.getHelper().attachToRecyclerView(recyclerView);

        url = intent.getStringExtra("tournamentURL");
        ChallongeRequests.sendRequest(response -> initPlayerList(response), ChallongeRequests.participantIndex(url));
        return v;
    }


    public void initPlayerList(String jsonString) {

        players.clear();

        JsonParser jsonParser = new JsonParser();
        JsonElement tournament = jsonParser.parse(jsonString);
        JsonArray participants = tournament.getAsJsonArray();


        for (JsonElement participant : participants) {
            Participant player = new Participant();
            JsonObject participantObject = participant.getAsJsonObject().get("participant").getAsJsonObject();

            player.setId(participantObject.get("id").getAsInt());
            player.setName(participantObject.get("name").getAsString());
            player.setSeed(participantObject.get("seed").getAsInt());
            player.setTournamentID(participantObject.get("tournament_id").getAsString());
            players.add(player);
            playerSeeds.add(String.valueOf(player.getSeed()));

        }


        adapter.notifyDataSetChanged();
    }


}
