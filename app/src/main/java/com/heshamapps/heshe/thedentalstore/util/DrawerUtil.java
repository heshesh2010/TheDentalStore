package com.heshamapps.heshe.thedentalstore.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

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
                            .addDrawerItems(mItemStore,mItemCart,mItemLogout, new DividerDrawerItem(),mItemDoctorOrderSetting,mItemAbout)
                            .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                                onNavDrawerItemSelected((int)drawerItem.getIdentifier());
                                return true;
                            })
                            .build();
                    mDrawerResult.deselect(mItemLogin.getIdentifier());
                    mCurrentProfile = checkCurrentProfileStatus();
                    break;
                default:
                  //  Toasty.error(getApplicationContext(), "Sorry, Unknown user type please contact app developer", Toast.LENGTH_LONG).show();
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
        }
        mDrawerResult.closeDrawer();
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
        mItemAdminOrderSetting = new PrimaryDrawerItem().withIdentifier(7).withName(R.string.adminOrderSetting).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));
        mItemDoctorOrderSetting = new PrimaryDrawerItem().withIdentifier(8).withName(R.string.settings).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));
        mItemAbout = new PrimaryDrawerItem().withIdentifier(9).withName(R.string.about).withIcon(activity.getResources().getDrawable(R.mipmap.ic_settings_black_48dp));


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
                activity.startActivity(new Intent(activity.getApplicationContext(), LoginActivity.class));
                activity.finish();
                //Sign Out
                break;
            case 6:
               signOutUser();
                break;

            // AdminOrders
            case 7:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new ViewOrdersFragment()).commit();
                break;

            //DoctorOrder
            case 8:
                Toast.makeText(activity, "Settings menu selected", Toast.LENGTH_LONG).show();
                break;

                // about
            case 9:
              //  activity.getFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new aboutFragment()).commit();
                Toast.makeText(activity, "about menu selected", Toast.LENGTH_LONG).show();
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


    private static void signOutUser() {
        //Sign out
        mFirebaseAuth.signOut();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (isUserSignedIn()) {

            mDrawerResult.updateItemAtPosition(mItemLogin, 1);
            mDrawerResult.removeItemByPosition(2);

            mDrawerResult.deselect(mItemLogin.getIdentifier());
            refreshMenuHeader();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        } else {
            //check if internet connectivity is there
        }
    }



}
