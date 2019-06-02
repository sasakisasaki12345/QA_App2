package com.example.qa_app2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.activity_question_detail.progressBar
import kotlinx.android.synthetic.main.activity_question_send.*
import kotlin.collections.HashMap

class QuestionDetailActivity : AppCompatActivity(),DatabaseReference.CompletionListener {

    private lateinit var mQuestion: Question
    private lateinit var mAdapterNonUser: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private var favoriteArrayList = ArrayList<Favorite>()

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            var map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)

            mQuestion.answers.add(answer)

            mAdapterNonUser.notifyDataSetChanged()


        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

    }

    override fun onResume() {
        super.onResume()
        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras


        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        mAdapterNonUser = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapterNonUser
        mAdapterNonUser.notifyDataSetChanged()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            var favorite_frag: String = "false"
            var favorite_serch: String = "false"

            var mDataBaseReference = FirebaseDatabase.getInstance().reference
            var favoriteRef = mDataBaseReference.child(FavoritePATH).child(user!!.uid)
            var mQuestionUid = mQuestion.questionUid
            favoriteRef!!.addChildEventListener(m2EventListener)

                for (favorite in favoriteArrayList) {
                    if (mQuestionUid == favorite.questionUid) {
                        favorite_serch = "true"
                        Log.d("a", "favoritefragをtrueに変更")
                    } else {
                        Log.d("a", "favoritefragはfalseのまま")
                    }
                }

                if (favorite_serch == "true") {
                    //trueつまり一致するものがあった際はお気に入りすでにされている状態のボタン表記
                    favorite_button.setImageResource(R.drawable.abc_ic_star_black_36dp)
                    favorite_frag = "false"
                } else {
                    //falseはつまり、一致するものがなかった際はお気に入りまだされていない状態のボタン表記
                    favorite_button.setImageResource(R.drawable.abc_ic_star_half_black_36dp)
                    favorite_frag = "true"
                }

           // }
           favorite_button.setOnClickListener {
               var a = findViewById<View>(R.id.favorite_button)

               var mDataBaseReference = FirebaseDatabase.getInstance().reference
               var favoriteRef = mDataBaseReference.child(FavoritePATH).child(user!!.uid)
               var mQuestionUid = mQuestion.questionUid
               var mGenre = mQuestion.genre.toString()
               favoriteRef!!.addChildEventListener(m2EventListener)
               var favoriteUid:String?=null
               val data = HashMap<String, String>()
               data["questionUid"]=mQuestionUid
               data["genre"]=mGenre
               /*
               if (favoriteArrayList==null){
                   favorite_button.setImageResource(R.drawable.abc_ic_star_black_36dp)
                   Toast.makeText(applicationContext,"お気に入り登録いたしました",Toast.LENGTH_LONG).show()
                   favoriteRef.push().setValue(data,this)
               }else {
               */
                   favorite_serch="false"
                   for (favorite in favoriteArrayList) {
                       if (mQuestionUid == favorite.questionUid) {
                           favorite_serch = "true"
                           favoriteUid=favorite.favoriteUid
                           Log.d("a", "favoritefragをtrueに変更")
                       } else {
                           Log.d("a", "favoritefragはfalseのまま")
                       }
                   }


                   if (favorite_serch=="true") {
                       //trueつまりすでに登録があった場合：それを消す
                       favorite_button.setImageResource(R.drawable.abc_ic_star_half_black_36dp)
                       Toast.makeText(applicationContext,"お気に入り登録から消しました",Toast.LENGTH_LONG).show()
                       val favoriteDRef= mDataBaseReference.child(FavoritePATH).child(user!!.uid).child(favoriteUid!!)
                       favoriteDRef.removeValue()
                   } else {
                       //falseつまり登録がなかった場合：それを登録する
                       favorite_button.setImageResource(R.drawable.abc_ic_star_black_36dp)
                       Toast.makeText(applicationContext,"お気に入り登録いたしました",Toast.LENGTH_LONG).show()
                       favoriteRef.push().setValue(data,this)
                   //}
               }
           }



                fab.setOnClickListener {

                    // ログイン済みのユーザーを取得する
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user == null) {
                        // ログインしていなければログイン画面に遷移させる
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Questionを渡して回答作成画面を起動する
                        val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                        intent.putExtra("question", mQuestion)
                        startActivity(intent)
                }
            }
        }else{
            var a = findViewById<View>(R.id.favorite_button)
            a.visibility=View.INVISIBLE
            Log.d("a", "fabを非表示")
        }

                val dataBaseReference = FirebaseDatabase.getInstance().reference
                mAnswerRef =
                    dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid)
                        .child(AnswersPATH)
                mAnswerRef.addChildEventListener(mEventListener)
    }
    private val m2EventListener = object : ChildEventListener {
        override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
            val map = datasnapshot.value as Map<String, String>
            var key = datasnapshot.key
            val questionUid = map["questionUid"]?:""
            val mGenre = map["genre"]?:""
            var favorite:Favorite = Favorite(questionUid, key,mGenre)
            favoriteArrayList.add(favorite)
        }

        override fun onChildChanged(datasnapshot: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(datasnapshot: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(datasnapshot: DataSnapshot) {
            var map = datasnapshot.value as Map<String,String>
            //var count : Int = 0

            var deleteFavorite :Favorite? = null

            //削除があったfavorite探す
            for(favorite in favoriteArrayList){
                if(favorite.favoriteUid!!.equals(datasnapshot.key)){
                    //DBで削除があったfavoriteを　allaylistから消す
                    //elemenneで消せるらしい。できなかったら、countで消す。
                    deleteFavorite = favorite
                    Log.d("a", "removedで消したfavoriteを見つけ出して変数に入れる")

                    //ループ中に消しちゃうのでエラーになっている

                }else{
                    //count += 1
                    //Log.d("a", "arraylist消す部分のカウントアップ＝"+count)
                }

            }
            favoriteArrayList.remove(deleteFavorite)
            Log.d("a", "deleteFavoriteを消す")

        }
    }

    override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {

    }

}