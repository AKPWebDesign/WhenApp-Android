package tech.akpmakes.android.taskkeeper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Calendar;

import tech.akpmakes.android.taskkeeper.util.DaysOfWeek;

public class SettingsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            addPreferencesFromResource(R.xml.pref_about);
            findPreference("app_version").setSummary(BuildConfig.VERSION_NAME);
            findPreference("os_version").setSummary("Android " + Build.VERSION.RELEASE + "; " + Build.DISPLAY);

            findPreference("app_version").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Snackbar.make(getActivity().findViewById(R.id.container), R.string.smiley,
                            Snackbar.LENGTH_LONG).show();
                    return true;
                }
            });

            findPreference("social_discord").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.social_discord_uri)));
                    startActivity(Intent.createChooser(i, null));
                    return true;
                }
            });

            findPreference("social_github").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.social_github_uri, getString(R.string.versionName))));
                    startActivity(Intent.createChooser(i, null));
                    return true;
                }
            });
			
			findPreference("privacy_policy").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_uri)));
                    startActivity(Intent.createChooser(i, null));
                    return true;
                }
            });

            findPreference("more_info").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_information_uri)));
                    startActivity(Intent.createChooser(i, null));
                    return true;
                }
            });

            findPreference("welcome_screen").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity().getApplicationContext(), WelcomeActivity.class);
                    i.putExtra("forced", true);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
            });
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat implements GoogleApiClient.OnConnectionFailedListener {
        private PreferenceManager pref;
        private static final int RC_SIGN_IN_GOOGLE = 0;
        private static final String TAG = "login";

        private GoogleApiClient mGoogleApiClient;
        private FirebaseAuth mAuth;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pref = new PreferenceManager(this.getContext());

            setHasOptionsMenu(true);

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity().getApplicationContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mAuth = FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().isAnonymous()) {
                addPreferencesFromResource(R.xml.pref_general_signed_out);
                final Preference sign_in = findPreference("sign_in_preference");
                sign_in.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        signInGoogle();
                        return true;
                    }
                });
            } else {
                addPreferencesFromResource(R.xml.pref_general_signed_in);
                final Preference sign_out = findPreference("sign_out_preference");
                sign_out.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        signOutGoogle();
                        return true;
                    }
                });
            }

            // We only want this setting if we can support using it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addPreferencesFromResource(R.xml.pref_day_of_week);
                final Preference dayOfWeek = findPreference("dayOfWeek_preference");
                updateDayOfWeek(dayOfWeek);
                dayOfWeek.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(GeneralPreferenceFragment.this.getContext())
                                .setTitle("Test")
                                .show();
                        return true;
                    }
                });
            }

            String package_name =  this.getActivity().getApplicationContext().getPackageName();
            final Uri uri = Uri.parse("market://details?id=" + (!package_name.endsWith(".debug") ? package_name : package_name.substring(0, package_name.length() - 6)));

            final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
            if (this.getActivity().getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                addPreferencesFromResource(R.xml.pref_general_rate);
                final Preference rate_app = findPreference("rate_preference");
                rate_app.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(rateAppIntent);
                        return true;
                    }
                });
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
            super.onStop();
            mGoogleApiClient.disconnect();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN_GOOGLE) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);
            }
        }

        private void updateDayOfWeek(Preference pref) {
            int dayValue = this.pref.getFirstDayOfWeek();
            boolean useDefault = dayValue == 0;
            if (useDefault) {
                dayValue = Calendar.getInstance().getFirstDayOfWeek();
            }
            pref.setSummary(getDayName(dayValue) + (useDefault ? " ("+getDayName(0)+")" : ""));
        }

        private String getDayName(int day) {
            return DaysOfWeek.get(day).getName(this.getContext());
        }

        private void handleGoogleSignInResult(GoogleSignInResult result) {
            Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
            }
            updateUI();
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            Log.d(TAG, "firebaseAuthWithGoogle:currentUid:" + mAuth.getCurrentUser().getUid());

            final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(getActivity().findViewById(R.id.container), "Authentication failed.",
                                    Snackbar.LENGTH_LONG).show();
                        }
                        updateUI();
                    }
                });
        }

        private void signInGoogle() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
        }

        private void signOutGoogle() {
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            updateUI();
                        }
                    });
        }

        private void updateUI() {
            getActivity().finish();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // An unresolvable error has occurred and Google APIs (including Sign-In) will not
            // be available.
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GeneralPreferenceFragment();
                case 1:
                    return new AboutPreferenceFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pref_header_general);
                case 1:
                    return getString(R.string.pref_header_about);
            }
            return null;
        }
    }
}
