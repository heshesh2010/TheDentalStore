package com.heshamapps.heshe.thedentalstore.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heshamapps.heshe.thedentalstore.Admin.ManageOrdersFragment;
import com.heshamapps.heshe.thedentalstore.Doctor.DentalStore.MyDentalStoreFragment;
import com.heshamapps.heshe.thedentalstore.Doctor.ViewOrdersFragment;
import com.heshamapps.heshe.thedentalstore.Login.LoginActivity;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.fragment.CartFragment;
import com.heshamapps.heshe.thedentalstore.fragment.MainFragment;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import static android.content.Context.MODE_PRIVATE;

public class DrawerUtil {


    private static Drawer mDrawerResult;
    private static AccountHeader mHeaderResult;
    private static ProfileDrawerItem mProfileDrawerItem;
    private static PrimaryDrawerItem mItemStore;
    private static PrimaryDrawerItem mItemCart;


    private static PrimaryDrawerItem mItemLogin;
    private static PrimaryDrawerItem mItemLogout;
    private static PrimaryDrawerItem mItemAdminOrderSetting;
    private static PrimaryDrawerItem mItemDoctorOrderSetting;
    private static PrimaryDrawerItem mItemMyDentalStore;
    private static PrimaryDrawerItem mItemVerifiedProfile;
    private static PrimaryDrawerItem mItemUnverifiedProfile;
    private static PrimaryDrawerItem mItemAbout;
    private static PrimaryDrawerItem mCurrentProfile;
    private static FirebaseUser mFirebaseUser;
    static FirebaseAuth mFirebaseAuth;
    private static UserSession session;

    @SuppressLint("StaticFieldLeak")
    private static FragmentActivity activity;

    public DrawerUtil(FragmentActivity activity, Toolbar mToolbar, FirebaseAuth mFirebaseAuth) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        DrawerUtil.mFirebaseUser =mFirebaseAuth.getCurrentUser();
        DrawerUtil.mFirebaseAuth=mFirebaseAuth;
        DrawerUtil.activity =activity;
        session = new UserSession(activity);
        instantiateMenuItems();
        setupProfileDrawer();


        if (isUserSignedIn()){
            switch (getUserType()) {
                case CONFIG.ADMIN:
                    mDrawerResult = new DrawerBuilder()
                            .withActivity(activity)
                            .withAccountHeader(setupAccountHeader())
                            .withToolbar(mToolbar)
                            .addDrawerItems(mItemStore,mItemCart,mItemLogout, new DividerDrawerItem(),mItemAdminOrderSetting,mItemAbout)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    onNavDrawerItemSelected((int)drawerItem.getIdentifier());
                                    return true;
                                }
                            })
                            .build();
                    mDrawerResult.deselect(mItemLogin.getIdentifier());
                    mCurrentProfile = checkCurrentProfileStatus();
                    break;
                case CONFIG.DOCTOR:
                    mDrawerResult = new DrawerBuilder()
                            .withActivity(activity)
                            .withAccountHeader(setupAccountHeader())
                            .withToolbar(mToolbar)
                            .addDrawerItems(mItemStore,mItemCart,mItemMyDentalStore,mItemLogout, new DividerDrawerItem(),mItemDoctorOrderSetting,mItemAbout)
                            .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                                onNavDrawerItemSelected((int)drawerItem.getIdentifier());
                                return true;
                            })
                            .build();
                    mDrawerResult.deselect(mItemLogin.getIdentifier());
                    mCurrentProfile = checkCurrentProfileStatus();
                    break;
                default:
                    mDrawerResult = new DrawerBuilder()
                            .withActivity(activity)
                            .withAccountHeader(setupAccountHeader())
                            .withToolbar(mToolbar)
                            .addDrawerItems( mItemStore,mItemCart,new DividerDrawerItem() ,mItemLogin, mItemAbout)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    onNavDrawerItemSelected((int)drawerItem.getIdentifier());
                                    return true;
                                }
                            })
                            .build();
                    mDrawerResult.closeDrawer();

                    break;
            }

        }else{ // if not logged in
            mDrawerResult = new DrawerBuilder()
                    .withActivity(activity)
                    .withAccountHeader(setupAccountHeader())
                    .withToolbar(mToolbar)
                    .addDrawerItems( mItemStore,mItemCart,new DividerDrawerItem() ,mItemLogin, mItemAbout)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            onNavDrawerItemSelected((int)drawerItem.getIdentifier());
                            return true;
                        }
                    })
                    .build();
            mDrawerResult.closeDrawer();
        }

    }

    private static PrimaryDrawerItem checkCurrentProfileStatus(){
        if (mFirebaseUser.isEmailVerified()){
            mCurrentProfile = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.verified_profile).withIcon(activity.getResources().getDrawable(R.mipmap.ic_verified_user_black_24dp));;
        }else{
            mCurrentProfile = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.unverified_profile).withIcon(activity.getResources().getDrawable(R.mipmap.ic_report_problem_black_24dp));
        }
        return mCurrentProfile;
    }

    private static boolean isUserSignedIn(){
        return session.isLoggedIn();
    }

    private static void instantiateMenuItems(){
        mItemVerifiedProfile = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.verified_profile).withIcon(activity.getResources().getDrawable(R.mipmap.ic_verified_user_black_24dp));
        mItemUnverifiedProfile = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.unverified_profile).withIcon(activity.getResources().getDrawable(R.mipmap.ic_report_problem_black_24dp));



        mItemStore = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.store).withIcon(activity.getResources().getDrawable(R.mipmap.ic_login_black_48dp));
       mItemCart= new PrimaryDrawerItem().withIdentifier(4).withName(R.string.cart).withIcon(activity.getResources().getDrawable(R.mipmap.ic_login_black_48dp));

        mItemLogin = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.login_menu_item).withIcon(activity.getResources().getDrawable(R.mipmap.ic_login_black_48dp));
        mItemLogout = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.logout_menu_item).withIcon(activity.getResources().getDrawable(R.mipmap.ic_logout_black_48dp));
        mItemAdminOrderSetting = new PrimaryDrawerItem().withIdentifier(8).withName(R.string.adminOrderSetting).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));
        mItemDoctorOrderSetting = new PrimaryDrawerItem().withIdentifier(7).withName(R.string.doctorOrderSetting).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));
        mItemAbout = new PrimaryDrawerItem().withIdentifier(9).withName(R.string.about).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));
        mItemMyDentalStore = new PrimaryDrawerItem().withIdentifier(10).withName(R.string.MyDentalStore).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));



    }


    private static AccountHeader setupAccountHeader(){
        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(mProfileDrawerItem)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                }).withSelectionListEnabledForSingleProfile(false)
                .build();
        return mHeaderResult;
    }

    private static void setupProfileDrawer() {
        //check if the user is logged in. If logged in, get details (name, email, pic etc) dynamically
        // pass the actual photo dynamically.

        if (mFirebaseUser != null ) {
            if(mFirebaseUser.getPhotoUrl()==null ){
                mProfileDrawerItem = new ProfileDrawerItem()
                        .withName(mFirebaseUser.getDisplayName())
                        .withEmail(mFirebaseUser.getEmail())
                        .withIcon((activity.getResources().getDrawable(R.mipmap.ic_account_circle_black_48dp)));
            }
            else{
                mProfileDrawerItem = new ProfileDrawerItem()
                        .withName(mFirebaseUser.getDisplayName())
                        .withEmail(mFirebaseUser.getEmail())
                        .withIcon(mFirebaseUser.getPhotoUrl());
                //initialize and create the image loader logic
                          /*  DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                                @Override
                                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                                    Picasso.with(imageView.getContext()).load(mFirebaseUser.getPhotoUrl()).placeholder(placeholder).into(imageView);
                                }

                                @Override
                                public void cancel(ImageView imageView) {
                                    Picasso.with(imageView.getContext()).cancelRequest(imageView);
                                }
                            });*/

            }} else {//else if the user is not logged in, show a default icon
            mProfileDrawerItem = new ProfileDrawerItem()
                    .withIcon(activity.getResources().getDrawable(R.mipmap.ic_account_circle_black_48dp));
        }
    }

    private static void onNavDrawerItemSelected(int drawerItemIdentifier){
        switch (drawerItemIdentifier){

            //store
            case 3:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new MainFragment()).commit();
                break;

            //cart
            case 4:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new CartFragment()).commit();
                break;

            //Sign In
            case 5:
                signInUser();
                Toast.makeText(activity, "Sign in menu selected", Toast.LENGTH_LONG).show();
                break;

            //Sign Out
            case 6:
               signOutUser();
                Toast.makeText(activity, "Sign out menu selected", Toast.LENGTH_LONG).show();
                break;

            // Doctor Orders
            case 7:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new ViewOrdersFragment()).commit();
                Toast.makeText(activity, "Doctor Orders menu selected", Toast.LENGTH_LONG).show();

                break;

            //Admin Order
            case 8:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new ManageOrdersFragment()).commit();
                Toast.makeText(activity, "Admin Manage Orders menu selected", Toast.LENGTH_LONG).show();
                break;

                // about
            case 9:
              //  activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new aboutFragment()).commit();
                Toast.makeText(activity, "About menu selected", Toast.LENGTH_LONG).show();
            break;
            // dental Store
            case 10:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new MyDentalStoreFragment()).commit();
                Toast.makeText(activity, "My dental menu selected", Toast.LENGTH_LONG).show();
                break;
        }
        mDrawerResult.closeDrawer();
    }


    private int getUserType(){

        SharedPreferences prefs = activity.getSharedPreferences("myPref", MODE_PRIVATE);

        return prefs.getInt("userType", 0);
    }

    private static void refreshMenuHeader(){
        mDrawerResult.closeDrawer();
        mHeaderResult.clear();
        setupProfileDrawer();
        setupAccountHeader();
        mDrawerResult.setHeader(mHeaderResult.getView());
        mDrawerResult.resetDrawerContent();
    }


    private static void intstantiateUser(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private static void signInUser(){
        intstantiateUser();
        mDrawerResult.closeDrawer();
        activity.startActivity(new Intent(activity.getApplicationContext(), LoginActivity.class));
        activity.finish();

     //   ((TextView)findViewById(R.id.idContent)).setText(R.string.welcome_on_signin);
    }


    private static void signOutUser() {
        //Sign out
        mFirebaseAuth.signOut();
       // mFirebaseUser = mFirebaseAuth.getCurrentUser();

        session.logoutUser();

        if (!isUserSignedIn()) {

            mDrawerResult.updateItemAtPosition(mItemLogin, 1);
            mDrawerResult.removeItemByPosition(5);

            mDrawerResult.deselect(mItemLogin.getIdentifier());
            refreshMenuHeader();
        } else {
            //check if internet connectivity is there
        }
    }



}
