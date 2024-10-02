package homes.gensokyo.enigma.ui.oobe

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
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

    private lateinit var gradeSpinAdapter: ArrayAdapter<String>
    private lateinit var classSpinAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gradeSpinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        classSpinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())

        val schoolAdapter = SchoolAdapter(settingViewModel,listOf<School>())
        enableEdgeToEdge()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OOBEActivity)
            adapter = schoolAdapter
        }

        settingViewModel.schoolData.observe(this, Observer { newData ->
            schoolAdapter.updateData(newData)
            binding.recyclerView.apply {
                //layoutManager = LinearLayoutManager(this@OOBEActivity)
                //adapter = schoolAdapter
                //settingViewModel.isSchoolListVisible.postValue(true)
                //可能新能提升
                binding.recyclerView.visibility = if (newData.isEmpty()) View.GONE else View.VISIBLE
            }
        })
        binding.ETCPName.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                settingViewModel.schoolInput.value = s?.toString() ?: ""
                Log.d("TAG", "${settingViewModel.schoolInput.value}")
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        setupClassSpinner()
        setupGradeSpinner()


        binding.button.setOnClickListener{
            /*
           //val enteredName = settingViewModel.inputtedName.value

            val enteredName = binding.nameet.text.toString()
            val selectedClassId = settingViewModel.selectedGradeId.value
            val enteredCardNumber = binding.CrdNumber.text.toString()
            enteredName?.toast()
            Log.d("OOBEActivity", "Name: $enteredName, ClassId: $selectedClassId")
            if (enteredName!!.isNotEmpty() && selectedClassId != null ) {
                if (enteredName != null) {
                    settingViewModel.submitStudentData(enteredName, selectedClassId,enteredCardNumber)
                }
                Log.d("OOBEActivity", "Name: $enteredName, ClassId: $selectedClassId")
                //get("isFirst", "yes").toast()
                SettingUtils.put("isFirst", false)
                Log.d("ST",get("isFirst",true).toString())
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Log.e("OOBEActivity", "Name or ClassId is missing!")
                "Name or ClassId is missing!".toast()
            }

             */
            handleSubmit()
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
                Log.d("TAG", "Invalid data")
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
    private fun handleSubmit() {
        val enteredName = binding.nameet.text.toString()
        val selectedClassId = settingViewModel.selectedGradeId.value
        val enteredCardNumber = binding.CrdNumber.text.toString()

        if (enteredName.isNotEmpty() && selectedClassId != null && enteredCardNumber.isNotEmpty()) {
            settingViewModel.submitStudentData(enteredName, selectedClassId, enteredCardNumber)
            Log.d("OOBEActivity", "Name: $enteredName, ClassId: $selectedClassId")
            SettingUtils.put("isFirst", false)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Log.e("OOBEActivity", "Name or ClassId is missing!")
            "请正确输入!".toast()
        }
    }
    private fun setupGradeSpinner() {
        settingViewModel.gradeNamesLiveData.observe(this, Observer { gradeITEM ->
            if (gradeITEM.isNullOrEmpty()) {
                Log.d("StET", "No class names available")
                return@Observer
            }
            Log.d("gradeName22", "$gradeITEM")
            val gradeNameList = gradeITEM.map { it.gradeName }
            gradeSpinAdapter.clear()
            gradeSpinAdapter.addAll(gradeNameList)

            binding.ETGrade.setAdapter(gradeSpinAdapter)
            binding.ETGrade.requestFocus()
            binding.ETGrade.showDropDown()
        })

        binding.ETGrade.setOnFocusChangeListener { _, hasFocus ->
                binding.ETGrade.showDropDown()

        }

        binding.ETGrade.setOnItemClickListener { parent, view, position, id ->
            val selectedGradeItem = settingViewModel.gradeNamesLiveData.value?.get(position)
            selectedGradeItem?.let {
                Log.d("StET", "Selected Grade ID: ${it.gradeId}, Grade Name: ${it.gradeName}")
                settingViewModel.onGradeSelected(it.gradeId)
            }
        }

    }
    private fun setupClassSpinner() {
        settingViewModel.classNamesLiveData.observe(this, Observer { classITEM ->
            if (classITEM.isNullOrEmpty()) {
                Log.d("StET", "No class names available")
                return@Observer
            }
            Log.d("className", "$classITEM")
            val classNameList = classITEM.map { it.className }
            classSpinAdapter.clear()
            classSpinAdapter.addAll(classNameList)

            binding.ETClass.setAdapter(classSpinAdapter)
            binding.ETClass.requestFocus()
            binding.ETClass.showDropDown()
        }
        )
        binding.ETClass.setOnFocusChangeListener { _, _ ->
            //if (hasFocus) binding.ETClass.showDropDown()
            binding.ETClass.showDropDown()
        }
        binding.ETClass.setOnItemClickListener { parent, view, position, id ->
            val selectedClassItem = settingViewModel.classNamesLiveData.value?.get(position)
            selectedClassItem?.let {
                Log.d("StET", "Selected Class ID: ${it.classId}, Class Name: ${it.className}")
                settingViewModel.onGradeSelected(it.classId)
            }
        }
    }

}
