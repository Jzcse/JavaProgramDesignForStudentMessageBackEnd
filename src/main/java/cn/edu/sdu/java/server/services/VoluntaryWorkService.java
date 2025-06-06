package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.VoluntaryWork;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.VoluntaryWorkRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VoluntaryWorkService {

    @Autowired
    private VoluntaryWorkRepository voluntaryWorkRepository;

    @Transactional
    public DataResponse addVoluntaryWork(@Valid DataRequest datarequest) {
        VoluntaryWork voluntaryWork = new VoluntaryWork();
        DataResponse dataResponse = new DataResponse();
        Map voluntaryWorkMap = datarequest.getMap("voluntaryWorkMap");
        if (voluntaryWorkMap == null||voluntaryWorkMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("新增志愿服务列表不能为空");
            return dataResponse;
        }else {
            voluntaryWork.setWorkName(CommonMethod.getString(voluntaryWorkMap,"workName"));
            voluntaryWork.setWorkType(CommonMethod.getString(voluntaryWorkMap,"workType"));
            voluntaryWork.setWorkLevel(CommonMethod.getString(voluntaryWorkMap,"workLevel"));
            voluntaryWork.setWorkTime(CommonMethod.getString(voluntaryWorkMap,"workTime"));
            voluntaryWork.setWorkSize(CommonMethod.getString(voluntaryWorkMap,"workSize"));
        }
        voluntaryWorkRepository.save(voluntaryWork);
        dataResponse.setCode(0);
        dataResponse.setMsg("新增志愿服务成功");
        dataResponse.setData(voluntaryWork);
        return dataResponse;
    }

    public Map getMapFromVoluntaryWork(VoluntaryWork voluntaryWork) {
        Map m = new HashMap();
        if (voluntaryWork == null)
            return m;
        else {
            m.put("workName", voluntaryWork.getWorkName());
            m.put("workType", voluntaryWork.getWorkType());
            m.put("workLevel", voluntaryWork.getWorkLevel());
            m.put("workTime", voluntaryWork.getWorkTime());
            m.put("workSize", voluntaryWork.getWorkSize());
            return m;
        }
    }

    public List getVoluntaryWorkMapList(String workName) {
        List dataList = new ArrayList();
        List<VoluntaryWork> voluntaryWorkList = voluntaryWorkRepository.findVoluntaryWorkByWorkName(workName);
        if (voluntaryWorkList.isEmpty()||voluntaryWorkList==null) {
            return null;
        }else {
            for (VoluntaryWork voluntaryWork : voluntaryWorkList) {
                dataList.add(getMapFromVoluntaryWork(voluntaryWork));
            }
        }
        return dataList;
    }

    public DataResponse getSelectedList(DataRequest dataRequest) {
        String workName = dataRequest.getString("workName");
        if (workName == null || workName.isEmpty()) {
            return CommonMethod.getReturnMessageError("查询条件不能为空");
        }
        List dataList = getVoluntaryWorkMapList(workName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    // 获取奖项列表
    public DataResponse getVoluntaryWorkList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        try {
            List<VoluntaryWork> workList = voluntaryWorkRepository.findAll();
            Map<Integer, VoluntaryWork> workMap = new HashMap<>();
            for (VoluntaryWork voluntaryWork : workList) {
                workMap.put(voluntaryWork.getWorkId(), voluntaryWork);
            }
            dataResponse.setData(workMap);
            dataResponse.setCode(0);
            dataResponse.setMsg("获取志愿服务列表成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("获取志愿服务列表失败: " + e.getMessage());
        }
        return dataResponse;
    }

    @Transactional
    public DataResponse deleteVoluntaryWork(@Valid DataRequest datarequest) {
        Integer workId = datarequest.getInteger("workId");
        DataResponse dataResponse = new DataResponse();
        if (workId == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("workId不能为空");
            return dataResponse;
        }
        try {
            Optional<VoluntaryWork> award = voluntaryWorkRepository.findById(workId);
            if (award.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该志愿服务不存在");
                return dataResponse;
            }
            voluntaryWorkRepository.deleteById(workId);
            dataResponse.setCode(0);
            dataResponse.setMsg("删除志愿服务成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("删除志愿服务失败: " + e.getMessage());
        }
        return dataResponse;
    }

    @Transactional
    public DataResponse updateVoluntaryWork(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map awardMap = datarequest.getMap("form");
        String workIdstr = CommonMethod.getString(awardMap, "workId");
        double workIdD = Double.parseDouble(workIdstr);
        Integer workId = (int) workIdD;
        if (awardMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("更新志愿服务列表不能为空");
            return dataResponse;
        }
        try {
            Optional<VoluntaryWork> voluntarywork = voluntaryWorkRepository.findVoluntaryWorkByWorkId(workId);
            if (voluntarywork.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该奖项不存在");
                return dataResponse;
            }
            VoluntaryWork workUpdate = voluntarywork.get();

            workUpdate.setWorkName(CommonMethod.getString(awardMap, "workName"));
            workUpdate.setWorkType(CommonMethod.getString(awardMap, "workType"));
            workUpdate.setWorkLevel(CommonMethod.getString(awardMap, "workLevel"));
            workUpdate.setWorkTime(CommonMethod.getString(awardMap, "workTime"));
            workUpdate.setWorkSize(CommonMethod.getString(awardMap, "workSize"));

            voluntaryWorkRepository.save(workUpdate);
            dataResponse.setCode(0);
            dataResponse.setMsg("修改志愿服务成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("修改志愿服务失败: " + e.getMessage());
        }
        return dataResponse;
    }
}
