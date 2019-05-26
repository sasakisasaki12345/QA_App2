package com.example.qa_app2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class QuestionDetailListAdapterLogin(context: Context, private val mQustion: Question) : BaseAdapter() {
    private lateinit var mFavoriteRef :DatabaseReference
    private lateinit var mDatabaseReference:DatabaseReference
    private var favoriteArrayList = ArrayList<Favorite>()

    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_FAVORITE = 1
        private val TYPE_ANSWER = 2
    }

    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 2 + mQustion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_QUESTION
        } else if(position == 1){
            TYPE_FAVORITE
        }else{
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQustion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }


        }else if(getItemViewType(position) == TYPE_FAVORITE ){
            if (convertView == null){
                convertView = mLayoutInflater!!.inflate(R.layout.list_favorite,parent,false)!!
            }

            /*val button_favorite = convertView.findViewById<View>(R.id.button_favorite) as Button
            button_favorite.setOnClickListener {

                val user = FirebaseAuth.getInstance().currentUser
                val dataBaseReference = FirebaseDatabase.getInstance().reference
                val questionId:String = ""
                val favoriteRef = dataBaseReference.child(FavoritePATH).child(user!!.uid)
                val mQuestionUid = mQustion.questionUid
                val data = HashMap<String,String>()

                //favoriteRef!!.addChildEventListener(mEventListener)


                var favorite_flag ="false"
                val status:String="false"
                data["questionId"]= mQuestionUid


                if (favoriteRef == null) {
                    //そもそもお気に入りが一つもない場合
                    //dataに入れる
                    favorite_flag ="false"
                    data["status"]="true"
                } else {
                    mFavoriteRef = mDatabaseReference.child(FavoritePATH).child(user!!.uid)
                    mFavoriteRef!!.addChildEventListener(mEventListener)
                    //質問がお気に入り登録されてるか判断
                    //forでfavoritesを一周
                    for (favorite in favoriteArrayList) {
                      if (mQuestionUid == favorite.questionUid) {
                         favorite_flag = "true"
                    } else {

                      }
                    }

                    //されてたタイミングでfrag「true」に変える
                    //変えたあとにfragがfalseに変わられないようにelseは空で

                    if (favorite_flag == "false") {
                        //未登録の状態でクリックされたということ
                        //つまりお気に入り未登録→登録のアクションを書く
                        button_favorite.text = "お気に入り済み\nお気に入り登録から外す"//字変える
                        //登録内容変える「登録する」
                        data["status"]="true"

                    } else {
                        //登録の状態でクリックされたということ
                        //つまりお気に入り未登録→登録のアクションを書く
                        button_favorite.text = "お気に入り未登録\nお気に入り登録する"//字変える
                        ////登録内容変える「登録から外す」
                        data["status"]="false"
                    }
                }
                favoriteRef.push().setValue(data, this)
                }
                */

            return convertView!!



          }else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer, parent, false)!!
            }

            val answer = mQustion.answers[position - 1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name
        }

        return convertView!!
    }

    /*
    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
            val map = datasnapshot.value as Map<String, String>
            val questionUid = map["questionUid"]
            val status = map["status"]
            var favorite = Favorite(questionUid, status)
            favoriteArrayList.add(favorite)
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        }
        */

    }