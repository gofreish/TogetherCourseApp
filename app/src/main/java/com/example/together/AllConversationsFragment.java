package com.example.together;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.together.model.User;
import com.example.together.recycler_views.conversation.ConversationAdapter;
import com.example.together.service.UserService;
import com.example.together.shared.executors.UserExecutor;
import com.example.together.shared.executors.UserListExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllConversationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllConversationsFragment extends Fragment {

    RecyclerView conversationRecycler;
    List<User> allUsers = new ArrayList<User>();
    ConversationAdapter conversationAdapter;

    public AllConversationsFragment() {
        // Required empty public constructor
    }

    public static AllConversationsFragment newInstance(String param1, String param2) {
        AllConversationsFragment fragment = new AllConversationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
/*
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
        allUsers.add(new User("aaa", "Test user 1", "Email@email.com", "https://firebasestorage.googleapis.com/v0/b/together-702dc.appspot.com/o/images%2Fimages%2Fced4a498-524f-4410-8f62-29269f17988a.jpg?alt=media&token=dcc766fc-8c49-46b6-aeb5-afec08493ad2"));
*/
        UserListExecutor allUsersGetted = new UserListExecutor() {
            @Override
            public void executor(List<User> users) {
                allUsers = users;
                allUsers.addAll(users);
                conversationAdapter.notifyItemRangeInserted(0, allUsers.size());
            }
        };
        UserListExecutor getAllUsersFail = new UserListExecutor() {
            @Override
            public void executor(List<User> users) {
                //allUsers = users;
                Toast.makeText(requireActivity(), "Impossible de charger les utilisateurs", Toast.LENGTH_SHORT).show();
            }
        };
        UserExecutor newUserExecutor = new UserExecutor() {
            @Override
            public void executor(User user) {
                allUsers.add(user);
                conversationAdapter.notifyItemInserted(allUsers.size()-1);
            }
        };
        UserExecutor changedUserExecutor = new UserExecutor() {
            @Override
            public void executor(User user) {
                for(int i=0; i< allUsers.size(); i++){
                    User currentUser = allUsers.get(i);
                    if(currentUser.getUser_id().equals(user.getUser_id())){
                        currentUser.setUser_id(user.getUser_id());
                        currentUser.setUsername(user.getUsername());
                        currentUser.setEmail(user.getEmail());
                        currentUser.setProfile_img_url(user.getProfile_img_url());
                        conversationAdapter.notifyItemChanged(i, currentUser);
                        break;
                    }
                }
            }
        };
        UserService userService = UserService.getInstance();
        userService.firstGetAll(allUsersGetted, getAllUsersFail);
        userService.allUsersEventListener(newUserExecutor, changedUserExecutor);

        conversationAdapter = new ConversationAdapter(getContext().getApplicationContext(), allUsers);

        conversationRecycler = view.findViewById(R.id.convRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        conversationRecycler.setLayoutManager(layoutManager);
        conversationRecycler.setAdapter(conversationAdapter);

    }
}