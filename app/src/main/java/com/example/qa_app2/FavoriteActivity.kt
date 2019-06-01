package com.example.qa_app2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
    }

    override fun onResume() {
        super.onResume()


        var mQuestionFavoRef = mDatabaseReference.child(FavoritePATH).child(user!!.uid)


        //favoriteに入っているもの一式を取得しfavoriteArrayListに入れる
        mQuestionArrayList.clear()
        var mGenreRef1 = mDatabaseReference.child(ContentsPATH).child("1") //1のリファレンス開く
        var mGenreRef2 = mDatabaseReference.child(ContentsPATH).child("2")//2のリファレンス開く
        var mGenreRef3 = mDatabaseReference.child(ContentsPATH).child("3")//3のリファレンス開く
        var mGenreRef4 = mDatabaseReference.child(ContentsPATH).child("4") //4のリファレンス開く
        mQuestionFavoRef.addChildEventListener(m2EventListener)

        for (favoriteSelect in favoriteArrayList) {//favorite一つずつ取り出し

            //1のやつチェック
            mGenreRef1.addChildEventListener(mEventListener)//1を取り出す
            for (question in mQuestionArrayList) {//forで１の中のやつ一つずつ取り出す
                if (favoriteSelect.questionUid == question.questionUid) {//ifでfavoriteSelect.questionId==question.key
                    questionFavoriteArraylist.add(question)//trueならmfavoriteArrayList.add(question)
                }
            }
            //2のやつチェック
            mGenreRef2.addChildEventListener(mEventListener)//1を取り出す
            for (question in mQuestionArrayList) {//forで１の中のやつ一つずつ取り出す
                if (favoriteSelect.questionUid == question.questionUid) {//ifでfavoriteSelect.questionId==question.key
                    questionFavoriteArraylist.add(question)//trueならmfavoriteArrayList.add(question)
                }
            }

            //3のやつチェック
            mGenreRef3.addChildEventListener(mEventListener)//1を取り出す
            for (question in mQuestionArrayList) {//forで１の中のやつ一つずつ取り出す
                if (favoriteSelect.questionUid == question.questionUid) {//ifでfavoriteSelect.questionId==question.key
                    questionFavoriteArraylist.add(question)//trueならmfavoriteArrayList.add(question)
                }
            }

            //4のやつチェック
            mGenreRef4.addChildEventListener(mEventListener)//1を取り出す
            for (question in mQuestionArrayList) {//forで１の中のやつ一つずつ取り出す
                if (favoriteSelect.questionUid == question.questionUid) {//ifでfavoriteSelect.questionId==question.key
                    questionFavoriteArraylist.add(question)//trueならmfavoriteArrayList.add(question)
                }
            }
        }
        //全てのfavoriteArrayListのチェックが終わったらそのリストをアダプターいセット
        mAdapter.setQuestionArrayList(questionFavoriteArraylist)
        mListView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    private val m2EventListener = object : ChildEventListener {
        override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
            val map = datasnapshot.value as Map<String, String>
            var key = datasnapshot.key
            val questionUid = map["questionUid"]
            var favorite: Favorite = Favorite(questionUid, key)
            favoriteArrayList.add(favorite)
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
            for (favorite in favoriteArrayList) {
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
            favoriteArrayList.remove(deleteFavorite)
            Log.d("a", "deleteFavoriteを消す")

        }
    }

}
