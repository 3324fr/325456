package inf8405_tp2.tp2;


import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    // data object we want to retain
    private User user;

    private Group group;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }



    public User getUser() { return user; }

    public Group getGroup() { return group; }

    public void set(Group data) {
        this.group = data;
    }

    public void set(User data) { this.user = data; }
}
