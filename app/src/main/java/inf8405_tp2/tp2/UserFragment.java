package inf8405_tp2.tp2;


import android.app.Fragment;
import android.os.Bundle;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    // data object we want to retain
    private User user;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(User data) {
        this.user = data;
    }

    public User getData() {
        return user;
    }
}
