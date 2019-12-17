package com.kelompok.android;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelompok.android.adapters.CommentsAdapter;
import com.kelompok.android.adapters.DirectorApater;
import com.kelompok.android.adapters.DownloadAdapter;
import com.kelompok.android.adapters.EpisodeAdapter;
import com.kelompok.android.adapters.HomePageAdapter;
import com.kelompok.android.adapters.LiveTvHomeAdapter;
import com.kelompok.android.adapters.ServerApater;
import com.kelompok.android.models.CommentsModel;
import com.kelompok.android.models.CommonModels;
import com.kelompok.android.models.EpiModel;
import com.kelompok.android.models.SubtitleModel;
import com.kelompok.android.utils.ApiResources;
import com.kelompok.android.utils.BannerAds;
import com.kelompok.android.utils.PopUpAds;
import com.kelompok.android.utils.ToastMsg;
import com.kelompok.android.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.gms.ads.AdActivity.CLASS_NAME;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvName,tvDirector,tvRelease,tvCast,tvDes,tvGenre,tvRelated;


    private RecyclerView rvDirector,rvServer,rvRelated,rvComment,rvDownload;

    public static RelativeLayout lPlay;


    private DirectorApater directorApater;
    private ServerApater serverAdapter;
    private DownloadAdapter downloadAdapter;
    private EpisodeAdapter episodeAdapter;
    private HomePageAdapter relatedAdapter;
    private LiveTvHomeAdapter relatedTvAdapter;


    private List<CommonModels> listDirector =new ArrayList<>();
    private List<CommonModels> listEpisode =new ArrayList<>();
    private List<CommonModels> listRelated =new ArrayList<>();
    private List<CommentsModel> listComment =new ArrayList<>();
    private List<CommonModels> listDownload =new ArrayList<>();


    private String strDirector="",strCast="",strGenre="";
    public static LinearLayout llBottom,llBottomParent,llcomment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String type="",id="";

    private ImageView imgAddFav;

    public static ImageView imgBack;

    private String V_URL = "";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;


    private ShimmerFrameLayout shimmerFrameLayout;

    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;

    private String commentURl;
    private RelativeLayout adView;
    private InterstitialAd mInterstitialAd;
    private LinearLayout download_text;


    public static SimpleExoPlayer player;
    public static PlayerView simpleExoPlayerView;
    public static SubtitleView subtitleView;

    public static ImageView imgFull;

    public static boolean isPlaying,isFullScr;
    public static View playerLayout;

    private int playerHeight;
    public static boolean isVideo=true;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String strSubtitle="Null";
    public static MediaSource mediaSource=null;
    public static ImageView imgSubtitle;
    private List<SubtitleModel> listSub=new ArrayList<>();
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "details_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        adView=findViewById(R.id.adView);
        llBottom=findViewById(R.id.llbottom);
        tvDes=findViewById(R.id.tv_details);
        tvCast=findViewById(R.id.tv_cast);
        tvRelease=findViewById(R.id.tv_release_date);
        tvName=findViewById(R.id.text_name);
        tvDirector=findViewById(R.id.tv_director);
        tvGenre=findViewById(R.id.tv_genre);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        imgAddFav=findViewById(R.id.add_fav);
        imgBack=findViewById(R.id.img_back);
        webView=findViewById(R.id.webView);
        progressBar=findViewById(R.id.progressBar);
        llBottomParent=findViewById(R.id.llbottomparent);
        lPlay=findViewById(R.id.play);
        rvRelated=findViewById(R.id.rv_related);
        tvRelated=findViewById(R.id.tv_related);
        shimmerFrameLayout=findViewById(R.id.shimmer_view_container);
        btnComment=findViewById(R.id.btn_comment);
        etComment=findViewById(R.id.et_comment);
        rvComment=findViewById(R.id.recyclerView_comment);
        llcomment=findViewById(R.id.llcomments);
        simpleExoPlayerView = findViewById(R.id.video_view);
        subtitleView=findViewById(R.id.subtitle);
        playerLayout=findViewById(R.id.player_layout);
        imgFull=findViewById(R.id.img_full_scr);
        rvServer=findViewById(R.id.rv_server_list);
        rvDownload=findViewById(R.id.rv_download_list);
        imgSubtitle=findViewById(R.id.img_subtitle);
        download_text = findViewById(R.id.download_text);

        shimmerFrameLayout.startShimmer();


        playerHeight = lPlay.getLayoutParams().height;


        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






        type = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");


        final SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);

        if (preferences.getBoolean("status",false)){
            imgAddFav.setVisibility(VISIBLE);
        }else {
            imgAddFav.setVisibility(GONE);
        }


        commentsAdapter=new CommentsAdapter(this,listComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.setAdapter(commentsAdapter);

        commentURl = new ApiResources().getCommentsURL().concat("&&id=").concat(id);

        getComments(commentURl);




        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFullScr){
                    isFullScr=false;
                    swipeRefreshLayout.setVisibility(VISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    if (isVideo){
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));

                    }else {
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
                    }

                }else {

                    isFullScr=true;
                    swipeRefreshLayout.setVisibility(GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    if (isVideo){
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                    }else {
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                    }


                }


            }
        });


        imgSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                showDialog(DetailsActivity.this,listSub);

            }
        });



        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!preferences.getBoolean("status",false)){
                    startActivity(new Intent(DetailsActivity.this,LoginActivity.class));
                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.login_first));
                }

                else if (etComment.getText().toString().equals("")){

                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.comment_empty));

                }else {

                    String commentUrl = new ApiResources().getAddComment()
                            .concat("&&videos_id=")
                            .concat(id).concat("&&user_id=")
                            .concat(preferences.getString("id","0"))
                            .concat("&&comment=").concat(etComment.getText().toString());


                    addComment(commentUrl);

                }

            }
        });

        imgAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = new ApiResources().getAddFav()+"&&user_id="+preferences.getString("id","0")+"&&videos_id="+id;

                if (isFav){
                    String removeURL = new ApiResources().getRemoveFav()+"&&user_id="+preferences.getString("id","0")+"&&videos_id="+id;
                    removeFromFav(removeURL);
                }else {
                    addToFav(url);
                }
            }
        });


        if (!isNetworkAvailable()){
            new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.no_internet));
        }

        initGetData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                clear_previous();
                initGetData();
            }
        });

        loadAd();

    }

    void clear_previous(){
        strCast="";
        strDirector="";
        strGenre="";
        listDownload.clear();
    }


    public void showDialog(Context context, List<SubtitleModel> list){


        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_subtitle, viewGroup, false);

        ImageView cancel = dialogView.findViewById(R.id.cancel);


        RecyclerView recyclerView=dialogView.findViewById(R.id.recyclerView);
        SubtitleAdapter adapter= new SubtitleAdapter(context,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    private class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.OriginalViewHolder> {

        private List<SubtitleModel> items = new ArrayList<>();
        private Context ctx;

        public SubtitleAdapter(Context context, List<SubtitleModel> items) {
            this.items = items;
            ctx = context;
        }


        @Override
        public SubtitleAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SubtitleAdapter.OriginalViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subtitle, parent, false);
            vh = new SubtitleAdapter.OriginalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(SubtitleAdapter.OriginalViewHolder holder, final int position) {

            final SubtitleModel obj = items.get(position);
            holder.name.setText(obj.getLang());

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedSubtitle(mediaSource,obj.getUrl(),ctx);
                    alertDialog.cancel();

                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class OriginalViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            private View lyt_parent;


            public OriginalViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                lyt_parent=v.findViewById(R.id.lyt_parent);
            }
        }


    }


    private void loadAd(){


        if (ApiResources.adStatus.equals("1")){

            BannerAds.ShowBannerAds(this, adView);
            PopUpAds.ShowInterstitialAds(this);

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(ApiResources.adMobInterstitialId);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());


            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    Random rand = new Random();
                    int i = rand.nextInt(10)+1;

                    if (i%2==0){
                        mInterstitialAd.show();
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);

                }
            });

        }
    }

    private void initGetData(){

        if (!type.equals("tv")){


            //----related rv----------
            relatedAdapter=new HomePageAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedAdapter);

            if (type.equals("tvseries")){

                rvRelated.removeAllViews();
                listRelated.clear();
                rvServer.removeAllViews();
                listDirector.clear();
                listEpisode.clear();

                episodeAdapter=new EpisodeAdapter(this,listDirector);
                rvServer.setLayoutManager(new LinearLayoutManager(this));
                rvServer.setHasFixedSize(true);
                rvServer.setAdapter(episodeAdapter);
                getSeriesData(type,id);

                if (listSub.size()==0){
                    imgSubtitle.setVisibility(GONE);
                }

            }else {

                rvServer.removeAllViews();
                listDirector.clear();
                rvRelated.removeAllViews();
                listRelated.clear();
                if (listSub.size()==0){
                    imgSubtitle.setVisibility(GONE);
                }

                //---server adapter----
                serverAdapter=new ServerApater(this,listDirector);
                rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                rvServer.setHasFixedSize(true);
                rvServer.setAdapter(serverAdapter);

                //---download adapter--------
                downloadAdapter=new DownloadAdapter(this,listDownload);
                rvDownload.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                rvDownload.setHasFixedSize(true);
                rvDownload.setAdapter(downloadAdapter);


                getData(type,id);

                final ServerApater.OriginalViewHolder[] viewHolder = {null};
                serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                        iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                        listSub.clear();
                        listSub.addAll(obj.getListSub());

                        if (listSub.size()!=0){
                            imgSubtitle.setVisibility(VISIBLE);
                        }

                        serverAdapter.chanColor(viewHolder[0],position);
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder[0] =holder;
                    }
                });
            }

            SharedPreferences sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);
            String url = new ApiResources().getFavStatusURl()+"&&user_id="+sharedPreferences.getString("id","0")+"&&videos_id="+id;

            if (sharedPreferences.getBoolean("status",false)){
                getFavStatus(url);
            }

        }else {

            imgSubtitle.setVisibility(GONE);
            llcomment.setVisibility(GONE);

            tvRelated.setText("All TV :");

            rvServer.removeAllViews();
            listDirector.clear();
            rvRelated.removeAllViews();
            listRelated.clear();

            //----related rv----------
            relatedTvAdapter=new LiveTvHomeAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedTvAdapter);



            imgAddFav.setVisibility(GONE);



            serverAdapter=new ServerApater(this,listDirector);
            rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvServer.setHasFixedSize(true);
            rvServer.setAdapter(serverAdapter);
            getTvData(type,id);
            llBottom.setVisibility(GONE);

            final ServerApater.OriginalViewHolder[] viewHolder = {null};
            serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                @Override
                public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                    iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                    serverAdapter.chanColor(viewHolder[0],position);
                    holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder[0] =holder;
                }
            });


        }
    }


    private void initWeb(String s){

        if (isPlaying){
            player.release();

        }

        progressBar.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setVisibility(VISIBLE);
        playerLayout.setVisibility(GONE);

    }


    public void iniMoviePlayer(String url,String type,Context context){



        Log.e("vTYpe :: ",type);



        if (type.equals("embed") || type.equals("vimeo") || type.equals("gdrive")){
            isVideo=false;
            initWeb(url);
        }else {
            isVideo=true;
            initVideoPlayer(url,context,type);
        }
    }




    public void initVideoPlayer(String url,Context context,String type){

        progressBar.setVisibility(VISIBLE);

        if (player!=null){
            player.release();

        }



        webView.setVisibility(GONE);
        playerLayout.setVisibility(VISIBLE);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory(bandwidthMeter);


        TrackSelector trackSelector = new
                DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setPlayer(player);

        simpleExoPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                Log.e("Visibil", String.valueOf(visibility));
                if (visibility==0){
                    imgBack.setVisibility(VISIBLE);
                    imgFull.setVisibility(VISIBLE);
                    if (listSub.size()!=0) {
                        imgSubtitle.setVisibility(VISIBLE);
                    }
                    //imgSubtitle.setVisibility(VISIBLE);
                }else {
                    imgBack.setVisibility(GONE);
                    imgFull.setVisibility(GONE);
                    imgSubtitle.setVisibility(GONE);
                }
            }
        });

        Uri uri = Uri.parse(url);

        if (type.equals("hls")){
            mediaSource = hlsMediaSource(uri,context);


        }else if (type.equals("youtube")){
            Log.e("youtube url  :: ",url);
            extractYoutubeUrl(url,context,18);
        }
        else if (type.equals("youtube-live")){
            Log.e("youtube url  :: ",url);
            extractYoutubeUrl(url,context,133);
        }
        else if (type.equals("rtmp")){
            mediaSource=rtmpMediaSource(uri);
        }else {
            mediaSource=mediaSource(uri,context);
        }

        player.prepare(mediaSource, true, false);

        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == Player.STATE_READY) {

                    isPlaying=true;
                    progressBar.setVisibility(View.GONE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));

                }
                else if (playbackState==Player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                    isPlaying=false;
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                }
                else if (playbackState==Player.STATE_BUFFERING) {
                    isPlaying=false;
                    progressBar.setVisibility(VISIBLE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else {
                    // player paused in any state
                    isPlaying=false;
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                }

            }
        });


    }


    private void extractYoutubeUrl(String url, final Context context, final int tag) {


        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = tag;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    Log.e("YOUTUBE::", String.valueOf(downloadUrl));

                    try {

                        MediaSource mediaSource = mediaSource(Uri.parse(downloadUrl),context);
                        player.prepare(mediaSource, true, false);


                    }catch (Exception e){

                    }


                }
            }
        }.extract(url, true, true);


    }


    private MediaSource rtmpMediaSource(Uri uri){

        MediaSource videoSource = null;



        RtmpDataSourceFactory dataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        return  videoSource;

    }


    private MediaSource hlsMediaSource(Uri uri,Context context){


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "oxoo"), bandwidthMeter);

        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        return videoSource;


    }


    private MediaSource mediaSource(Uri uri,Context context){
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer")).
                createMediaSource(uri);

    }

    public void setSelectedSubtitle(MediaSource mediaSource,String subtitle,Context context) {
        MergingMediaSource mergedSource;
        if (subtitle != null) {
            Uri subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.TEXT_VTT, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());


            MediaSource subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
            player.prepare(mergedSource, false, false);
            //resumePlayer();

        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }


    private void addToFav(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.getString("message"));
                        isFav=true;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                    }else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                    }

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }

    private void getTvData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;
        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);

                try {
                    tvName.setText(response.getString("tv_name"));
                    tvDes.setText(response.getString("description"));
                    V_URL=response.getString("stream_url");


                    CommonModels model=new CommonModels();
                    model.setTitle("HD");
                    model.setStremURL(V_URL);
                    model.setServerType(response.getString("stream_from"));
                    listDirector.add(model);



                    JSONArray jsonArray = response.getJSONArray("all_tv_channel");
                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listRelated.add(models);

                    }
                    relatedTvAdapter.notifyDataSetChanged();



                    JSONArray serverArray = response.getJSONArray("additional_media_source");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("url"));
                        models.setServerType(jsonObject.getString("source"));


                        listDirector.add(models);
                    }
                    serverAdapter.notifyDataSetChanged();


                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void getSeriesData(String vtype,String vId){

        String type = "&&type="+vtype;
        String id = "&id="+vId;
        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                try {
                    tvName.setText(response.getString("title"));
                    tvRelease.setText("Release On "+response.getString("release"));
                    tvDes.setText(response.getString("description"));

                    //----director---------------
                    JSONArray directorArray = response.getJSONArray("director");
                    for (int i = 0;i<directorArray.length();i++){
                        JSONObject jsonObject=directorArray.getJSONObject(i);
                        if (i==directorArray.length()-1){
                            strDirector=strDirector+jsonObject.getString("name");
                        }else {
                            strDirector=strDirector+jsonObject.getString("name")+",";
                        }
                    }
                    tvDirector.setText(strDirector);


                    //----cast---------------
                    JSONArray castArray = response.getJSONArray("cast");
                    for (int i = 0;i<castArray.length();i++){
                        JSONObject jsonObject=castArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strCast=strCast+jsonObject.getString("name");
                        }else {
                            strCast=strCast+jsonObject.getString("name")+",";
                        }
                    }
                    tvCast.setText(strCast);


                    //---genre---------------
                    JSONArray genreArray = response.getJSONArray("genre");
                    for (int i = 0;i<genreArray.length();i++){
                        JSONObject jsonObject=genreArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strGenre=strGenre+jsonObject.getString("name");
                        }else {
                            strGenre=strGenre+jsonObject.getString("name")+",";
                        }
                    }
                    tvGenre.setText(strGenre);

                    //----realted post---------------
                    JSONArray relatedArray = response.getJSONArray("related_tvseries");
                    for (int i = 0;i<relatedArray.length();i++){
                        JSONObject jsonObject=relatedArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("title"));
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setId(jsonObject.getString("videos_id"));
                        models.setVideoType("tvseries");

                        listRelated.add(models);
                    }
                    relatedAdapter.notifyDataSetChanged();



                    //----episode------------
                    JSONArray mainArray = response.getJSONArray("season");


                    for (int i = 0;i<mainArray.length();i++){
                        //epList.clear();

                        JSONObject jsonObject=mainArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        String season_name=jsonObject.getString("seasons_name");
                        models.setTitle(jsonObject.getString("seasons_name"));


                        Log.e("Season Name 1::",jsonObject.getString("seasons_name"));

                        JSONArray episodeArray=jsonObject.getJSONArray("episodes");
                        List<EpiModel> epList=new ArrayList<>();

                        for (int j=0;j<episodeArray.length();j++){

                            JSONObject object =episodeArray.getJSONObject(j);

                            EpiModel model=new EpiModel();
                            model.setSeson(season_name);
                            model.setEpi(object.getString("episodes_name"));
                            model.setStreamURL(object.getString("file_url"));
                            model.setServerType(object.getString("file_type"));
                            epList.add(model);
                        }
                        models.setListEpi(epList);
                        listDirector.add(models);

                        episodeAdapter=new EpisodeAdapter(DetailsActivity.this,listDirector);
                        rvServer.setAdapter(episodeAdapter);
                        episodeAdapter.notifyDataSetChanged();

                    }


                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }


    private void getData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;

        strCast="";
        strDirector="";
        strGenre="";


        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    int download_check = response.getInt("enable_download");
                    if (download_check==1){
                        download_text.setVisibility(VISIBLE);
                    }

                    tvName.setText(response.getString("title"));
                    tvRelease.setText("Release On "+response.getString("release"));
                    tvDes.setText(response.getString("description"));

                    //----director---------------
                    JSONArray directorArray = response.getJSONArray("director");
                    for (int i = 0;i<directorArray.length();i++){
                        JSONObject jsonObject=directorArray.getJSONObject(i);
                        if (i==directorArray.length()-1){
                            strDirector=strDirector+jsonObject.getString("name");
                        }else {
                            strDirector=strDirector+jsonObject.getString("name")+",";
                        }
                    }

                    tvDirector.setText(strDirector);

                    //----cast---------------
                    JSONArray castArray = response.getJSONArray("cast");
                    for (int i = 0;i<castArray.length();i++){
                        JSONObject jsonObject=castArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strCast=strCast+jsonObject.getString("name");
                        }else {
                            strCast=strCast+jsonObject.getString("name")+",";
                        }
                    }
                    tvCast.setText(strCast);


                    //---genre---------------
                    JSONArray genreArray = response.getJSONArray("genre");
                    for (int i = 0;i<genreArray.length();i++){
                        JSONObject jsonObject=genreArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strGenre=strGenre+jsonObject.getString("name");
                        }else {
                            strGenre=strGenre+jsonObject.getString("name")+",";
                        }
                    }
                    tvGenre.setText(strGenre);

                    //----server---------------
                    JSONArray serverArray = response.getJSONArray("videos");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("file_url"));
                        models.setServerType(jsonObject.getString("file_type"));


                        if (jsonObject.getString("file_type").equals("mp4")){
                            V_URL=jsonObject.getString("file_url");
                        }

                        //----subtitle-----------
                        JSONArray subArray = jsonObject.getJSONArray("subtitle");

                        if (subArray.length()!=0){

                            List<SubtitleModel> list=new ArrayList<>();

                            for (int j = 0;j<subArray.length();j++){
                                JSONObject subObject = subArray.getJSONObject(j);

                                SubtitleModel subtitleModel = new SubtitleModel();

                                //strSubtitle = subObject.getString("url");
                                subtitleModel.setUrl(subObject.getString("url"));
                                subtitleModel.setLang(subObject.getString("language"));

                                list.add(subtitleModel);
                            }
                            if (i==0){
                                listSub.addAll(list);
                            }

                            models.setListSub(list);


                        }else {
                            models.setSubtitleURL(strSubtitle);
                        }

                        //models.setSubtitleURL("null");

                        listDirector.add(models);
                    }
                    serverAdapter.notifyDataSetChanged();


                    //----download list---------
                    JSONArray downloadArray = response.getJSONArray("download_links");
                    for (int i = 0;i<downloadArray.length();i++){
                        JSONObject jsonObject=downloadArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("download_url"));
                        listDownload.add(models);
                    }
                    downloadAdapter.notifyDataSetChanged();



                    //----realted post---------------
                    JSONArray relatedArray = response.getJSONArray("related_movie");
                    for (int i = 0;i<relatedArray.length();i++){
                        JSONObject jsonObject=relatedArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("title"));
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setId(jsonObject.getString("videos_id"));
                        models.setVideoType("movie");

                        listRelated.add(models);
                    }
                    relatedAdapter.notifyDataSetChanged();

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }


    private void getFavStatus(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        isFav=true;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                        imgAddFav.setVisibility(VISIBLE);
                    }else {
                        isFav=false;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_border_24);
                        imgAddFav.setVisibility(VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void removeFromFav(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        isFav=false;
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_border_24);
                    }else {
                        isFav=true;
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.fetch_error));
            }
        });

        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addComment(String url){


        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equals("success")){

                        rvComment.removeAllViews();
                        listComment.clear();
                        getComments(commentURl);
                        etComment.setText("");

                    }else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError("can't comment now ! try later");
            }
        });

        VolleySingleton.getInstance(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }


    private void getComments(String url){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i =0;i<response.length();i++){

                    try {

                        JSONObject jsonObject=response.getJSONObject(i);

                        CommentsModel model=new CommentsModel();

                        model.setName(jsonObject.getString("user_name"));
                        model.setImage(jsonObject.getString("user_img_url"));
                        model.setComment(jsonObject.getString("comments"));
                        model.setId(jsonObject.getString("comments_id"));

                        listComment.add(model);

                        commentsAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(DetailsActivity.this).addToRequestQueue(jsonArrayRequest);

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("ACTIVITY:::","PAUSE"+isPlaying);

        if (isPlaying && player!=null){

            //Log.e("PLAY:::","PAUSE");

            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.e("ACTIVITY:::","STOP"+isPlaying);

//        if (isPlaying && player!=null){
//
//            Log.e("PLAY:::","PAUSE");
//
//            player.setPlayWhenReady(false);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("ACTIVITY:::","DESTROY");
        //releasePlayer();
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("ACTIVITY:::","RESUME");
        //startPlayer();
        if(player!=null){
            Log.e("PLAY:::","RESUME");
            player.setPlayWhenReady(true);
        }

    }

    public void releasePlayer() {

        if (player != null) {
            player.setPlayWhenReady(true);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            simpleExoPlayerView = null;
            System.out.println("releasePlayer");
        }
    }


}
