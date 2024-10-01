package homes.gensokyo.enigma.ui.oobe

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import homes.gensokyo.enigma.MainActivity
import homes.gensokyo.enigma.adapter.SchoolAdapter
import homes.gensokyo.enigma.bean.School
import homes.gensokyo.enigma.databinding.ActivitySettingBinding
import homes.gensokyo.enigma.util.SettingUtils
import homes.gensokyo.enigma.util.SettingUtils.get
import homes.gensokyo.enigma.util.TextUtils.toast


class OOBEActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()
    private lateinit var GradeSpinAdapter:ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        var GradeSpinAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
        var ClassSpinAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )

        val adpter = SchoolAdapter(settingViewModel,listOf<School>())
        enableEdgeToEdge()
        val rc = binding.recyclerView
        rc.apply {
            layoutManager = LinearLayoutManager(this@OOBEActivity)
            adapter = adpter
        }
        settingViewModel.schoolData.observe(this, Observer {
            newData -> adpter.updateData(newData)
            rc.apply {
                layoutManager = LinearLayoutManager(this@OOBEActivity)
                adapter = adpter
                settingViewModel.isSchoolListVisible.postValue(true)

            }
        })
        binding.ETCPName.addTextChangedListener(object:
        TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s?.toString() ?: ""
                settingViewModel.schoolInput.value = newText
                Log.i("TAG","$newText")
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        settingViewModel.classNamesLiveData.observe(this, Observer { classITEM ->
            if (classITEM.isNullOrEmpty()) {
                Log.i("StET", "No class names available")
                return@Observer
            }
            Log.i("className", "$classITEM")
            val classNameList = classITEM.map { it.className }
            ClassSpinAdapter.clear()
            ClassSpinAdapter.addAll(classNameList)

            binding.ETClass.setAdapter(ClassSpinAdapter)
            ClassSpinAdapter.notifyDataSetChanged()

            binding.ETClass.requestFocus()
            binding.ETClass.showDropDown()
            }
        )
        binding.ETClass.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ETClass.showDropDown()
            }
        }

        binding.ETClass.setOnItemClickListener { parent, view, position, id ->
            val selectedClassItem = settingViewModel.classNamesLiveData.value?.get(position)
            selectedClassItem?.let {
                Log.i("StET", "Selected Class ID: ${it.classId}, Class Name: ${it.className}")
                settingViewModel.onGradeSelected(it.classId)
            }
        }


        settingViewModel.gradeNamesLiveData.observe(this, Observer { gradeITEM ->
            if (gradeITEM.isNullOrEmpty()) {
                Log.i("StET", "No class names available")
                return@Observer
            }
            Log.i("gradeName22", "$gradeITEM")
            val classNameList = gradeITEM.map { it.gradeName }
            GradeSpinAdapter.clear()
            GradeSpinAdapter.addAll(classNameList)

            binding.ETGrade.setAdapter(GradeSpinAdapter)
            GradeSpinAdapter.notifyDataSetChanged()

            binding.ETGrade.requestFocus()
            binding.ETGrade.showDropDown()

            //Log.i("StET", "${gradeITEM}")
        })

        binding.ETGrade.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ETGrade.showDropDown()
            }
        }

        binding.ETGrade.setOnItemClickListener { parent, view, position, id ->
            val selectedGradeItem = settingViewModel.gradeNamesLiveData.value?.get(position)
            selectedGradeItem?.let {
                Log.i("StET", "Selected Grade ID: ${it.gradeId}, Grade Name: ${it.gradeName}")
                settingViewModel.onGradeSelected(it.gradeId)
            }
        }

        binding.button.setOnClickListener{
           //val enteredName = settingViewModel.inputtedName.value
            val enteredName = binding.nameet.text.toString()
            val selectedClassId = settingViewModel.selectedGradeId.value
            val enteredCardNumber = binding.CrdNumber.text.toString()
            enteredName?.toast()
            Log.i("OOBEActivity", "Name: $enteredName, ClassId: $selectedClassId")
            if (enteredName!!.isNotEmpty() && selectedClassId != null ) {
                if (enteredName != null) {
                    settingViewModel.submitStudentData(enteredName, selectedClassId,enteredCardNumber)
                }
                Log.i("OOBEActivity", "Name: $enteredName, ClassId: $selectedClassId")
                //get("isFirst", "yes").toast()
                SettingUtils.put("isFirst", false)
                Log.i("ST",get("isFirst",true).toString())
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Log.e("OOBEActivity", "Name or ClassId is missing!")
                "Name or ClassId is missing!".toast()
            }
        }



        //TODO 应该可以把不同的空间的配置封装起来？
/*

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

 */

/*
        val et_WXOA = binding.ETWXOA
        val tv_ntfc = binding.
        val bt_submit = binding.BTSubmit

        et_WXOA.addTextChangedListener()
//TODO 优化逻辑
        bt_submit.setOnClickListener {
            var isValid = true
            if (et_WXOA.text.toString().isEmpty()) {
                et_WXOA.error = "Invalid WXoa"
                isValid = false
            }

            if (!isValid) {
                Log.i("TAG", "Invalid data")
                tv_ntfc.text = "Invalid data"
                return@setOnClickListener
            } else {
                put("WXoa", et_WXOA.text.toString())
                tv_ntfc.text = ""
                put("isFirst",true)
                finish()
                //TODO 在这里加上判断WXoa核UUID是否正确（链接他们服务器）
            }
        }
    }

 */

    }
}
