package com.example.qa_app2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mToolbar: Toolbar
    private var mGenre = 0

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter
    private var favoriteArrayList = ArrayList<Favorite>()
    private var questionFavoriteArraylist = ArrayList<Question>()

    private var mGenreRef: DatabaseReference? = null

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>
            val title = map["title"] ?: ""
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""
            val imageString = map["image"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }

            val answerArrayList = ArrayList<Answer>()
            val answerMap = map["answers"] as Map<String, String>?
            if (answerMap != null) {
                for (key in answerMap.keys) {
                    val temp = answerMap[key] as Map<String, String>
                    val answerBody = temp["body"] ?: ""
                    val answerName = temp["name"] ?: ""
                    val answerUid = temp["uid"] ?: ""
                    val answer = Answer(answerBody, answerName, answerUid, key)
                    answerArrayList.add(answer)
                }
            }

            val question = Question(
                title, body, name, uid, dataSnapshot.key ?: "",
                mGenre, bytes, answerArrayList
            )
            mQuestionArrayList.add(question)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {


            //コード途中、書くところ違うかも。一応コメントアウトで保存
            /*
            val map = dataSnapshot.value as Map<String, String>

            // 変更があったQuestionを探す
            for (question in mQuestionArrayList) {
                if (dataSnapshot.key.equals(question.questionUid)) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.answers.clear()
                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            question.answers.add(answer)
                        }
                    }

                    mAdapter.notifyDataSetChanged()
                }
            }
            */
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>

            // 変更があったQuestionを探す
            for (question in mQuestionArrayList) {
                if (dataSnapshot.key.equals(question.questionUid)) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.answers.clear()
                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            question.answers.add(answer)
                        }
                    }

                    mAdapter.notifyDataSetChanged()
                }
            }

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Log.d("a","onCreateでアクティビティをセット")
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        */

        /*
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener { view ->

        // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
        if (mGenre == 0) {
            Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show()
        } else {

        }
        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            // ログインしていなければログイン画面に遷移させる
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        } else {
            val view = findViewById<View>(android.R.id.content)
            Snackbar.make(view, "ログイン済み", Snackbar.LENGTH_LONG).show()
            // ジャンルを渡して質問作成画面を起動する
            val intent = Intent(applicationContext, QuestionSendActivity::class.java)
            intent.putExtra("genre", mGenre)
            startActivity(intent)
        }
    }

        */

        //val user = FirebaseAuth.getInstance().currentUser

        // ナビゲーションドロワーの設定
        /*
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        */
/*
        //ログインしているか確認
            //していなかったらnav_view表示
        if(user == null ) {
            //変更なしでそのまま出力
        }else{
            //ログインしている場合、activity_mainのnav_viewのmenuを変更
            nav_view.menu =
        }
        */


        // Firebase
        /*mDatabaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val user = FirebaseAuth.getInstance().currentUser
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)

            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }
        */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()



        //メニューバーのための変更点
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            setContentView(R.layout.activity_main)
            Log.d("a", "onResumeでactivity_mainをセット")
        } else {
            setContentView(R.layout.activity_main_login)
            Log.d("a", "onResumeでactivity_main_loginをセット")
        }



        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        //val navigationView = findViewById<NavigationView>(R.id.nav_view_)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        mListView.setOnItemClickListener { parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val user = FirebaseAuth.getInstance().currentUser
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)

            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener { view ->

            // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
            if (mGenre == 0) {
                Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show()
            } else {

            }
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "ログイン済み", Snackbar.LENGTH_LONG).show()
                // ジャンルを渡して質問作成画面を起動する
                val intent = Intent(applicationContext, QuestionSendActivity::class.java)
                intent.putExtra("genre", mGenre)
                startActivity(intent)
            }
        }


        // 1:趣味を既定の選択とする
        if (mGenre == 0) {
            onNavigationItemSelected(navigationView.menu.getItem(0))
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_hobby) {
            mToolbar.title = "趣味"
            mGenre = 1
        } else if (id == R.id.nav_life) {
            mToolbar.title = "生活"
            mGenre = 2
        } else if (id == R.id.nav_health) {
            mToolbar.title = "健康"
            mGenre = 3
        } else if (id == R.id.nav_compter) {
            mToolbar.title = "コンピューター"
            mGenre = 4
        } else if (id == R.id.nav_favorite) {
            mToolbar.title = "お気に入り"
            mGenre = 5
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        var user = FirebaseAuth.getInstance().currentUser

        // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        if (mGenre == 5) {
            val intent = Intent(applicationContext, FavoriteActivity::class.java)
            startActivity(intent)

        } else {
            mQuestionArrayList.clear()
            mAdapter.setQuestionArrayList(mQuestionArrayList)
            mListView.adapter = mAdapter

            // 選択したジャンルにリスナーを登録する
            if (mGenreRef != null) {
                mGenreRef!!.removeEventListener(mEventListener)
            }
            mGenreRef = mDatabaseReference.child(ContentsPATH).child(mGenre.toString())
            mGenreRef!!.addChildEventListener(mEventListener)
        }

        return true
    }


}

