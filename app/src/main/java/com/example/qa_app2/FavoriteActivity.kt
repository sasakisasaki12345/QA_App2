package com.example.qa_app2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoriteActivity : AppCompatActivity(), DatabaseReference.CompletionListener {
    var mDatabaseReference = FirebaseDatabase.getInstance().reference
    var mQuestionArrayList = ArrayList<Question>()
    var mFavoriteQuestionArrayList = ArrayList<Favorite>()
    private lateinit var mListView: ListView
    private lateinit var mAdapter: QuestionsListAdapter

    var favorite:Favorite?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
    }

    override fun onResume() {
        super.onResume()
        Log.d("a","Resume開始")
        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        var user = FirebaseAuth.getInstance().currentUser
        var mQuestionFavoRef = mDatabaseReference.child(FavoritePATH).child(user!!.uid)

        mQuestionFavoRef.addChildEventListener(mEventListener)
        Log.d("a","EventListenerから帰還")

        //favoriteに入っているもの一式を取得しfavoriteArrayListに入れる

        //ログ出力
        for(favo in mFavoriteQuestionArrayList) {
            Log.d("a", "favorite内の情報取得")
            Log.d("a", "genre="+favo.genre+"questionUid="+favo.questionUid)
        }

       /*for (favorite in mFavoriteQuestionArrayList) {//favorite一つずつ取り出し

            var mQestionRef =mDatabaseReference.child(ContentsPATH).child(favorite.genre.toString()).child(favorite.questionUid.toString())
            mQestionRef.addChildEventListener(m2EventListener)

            //ログ出力
            for(ques in mQuestionArrayList) {
                Log.d("a", "questionArrayList内の情報取得")
                Log.d("a", "genre="+ques.genre+"questionUid="+ques.questionUid)
            }
        }

        */
        //全てのfavoriteArrayListのチェックが終わったらそのリストをアダプターいセット
        //mQuestionArrayList.clear()
        //mAdapter.setQuestionArrayList(mQuestionArrayList)
        //mListView.adapter = mAdapter
        //mAdapter.notifyDataSetChanged()
    }

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
            Log.d("a", "mEventLister開始")
            val map = datasnapshot.value as Map<String, String>
            var key = datasnapshot.key
            val questionUid = map["questionUid"]
            var mGenre = map["genre"]
            var favorite: Favorite = Favorite(questionUid, key,mGenre)
            //mFavoriteQuestionArrayList.add(favorite)
            Log.d("a", "mEventListerで取得した結果="+questionUid)

            var mQestionRef =mDatabaseReference.child(ContentsPATH).child(mGenre.toString()).child(questionUid.toString())
            mQestionRef.addChildEventListener(m2EventListener)

        }

        override fun onChildChanged(datasnapshot: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(datasnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(datasnapshot: DataSnapshot) {
            var map = datasnapshot.value as Map<String, String>
            //var count : Int = 0

            var deleteFavorite: Favorite? = null

            //削除があったfavorite探す
            for (favorite in mFavoriteQuestionArrayList) {
                if (favorite.favoriteUid!!.equals(datasnapshot.key)) {
                    //DBで削除があったfavoriteを　allaylistから消す
                    //elemenneで消せるらしい。できなかったら、countで消す。
                    deleteFavorite = favorite
                    Log.d("a", "removedで消したfavoriteを見つけ出して変数に入れる")

                    //ループ中に消しちゃうのでエラーになっている

                } else {
                    //count += 1
                    //Log.d("a", "arraylist消す部分のカウントアップ＝"+count)
                }

            }
            mFavoriteQuestionArrayList.remove(deleteFavorite)
            Log.d("a", "deleteFavoriteを消す")

        }
    }

    private val m2EventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.d("a", "m2EventLister開始")
            val map2 = dataSnapshot.value as Map<String, String>
            val title = map2["title"] ?: ""
            val body = map2["body"] ?: ""
            val name = map2["name"] ?: ""
            val uid = map2["uid"] ?: ""
            val imageString = map2["image"] ?: ""
            val genre=favorite!!.genre!!.toInt()

            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }

            var answerArrayList = ArrayList<Answer>()
            val answerMap = map2["answers"] as Map<String, String>?
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
                genre, bytes, answerArrayList
            )
            mQuestionArrayList.add(question)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

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

    override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {

    }

}
