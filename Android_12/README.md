---

<div align="center">
 
 💜 *AR Cruise*

 <a href='https://youtu.be/WxFwOcKZY74'>
  
 <img src='https://img.shields.io/badge/-YOUTUBE-ff2020?style=for-the-badge&logo=youtube'>
 
 </a>
  
</div>

---

# 👆 프로젝트 요구사항
- Object Modeling  
  - Triangular faces Model (Search or Make Model)  
- Apply Object Modeling Transform, UI

# 👨‍💻 사용 기술 Stack
- [x] Android Studio  
- [x] Java  
- [x] Vuforia SDK  

# ⌨️ Code (일부분)
```java  
ImageButton UpButton = (ImageButton) findViewById(R.id.UpButton);
UpButton.setOnClickListener(new ImageButton.OnClickListener() {
    @Override
    public void onClick(Viec v) {
        A = 1;
        
        point_z = point_z - 0.003f; // Cruise 위치 조정
        headangle = 90.0f; // Cruise 머리 방향 조정
    }
});
// 중략...
if (point_x >= 0.075f) point_x = 0.075f; 
if (point_x <= -0.075f) point_x = -0.075f;

if (point_z >= 0.054f) point_z = 0.054f;
if (point_z <= -0.048f) point_z = -0.048f; // 위치 Boundary 및 원근 효과 설정
// 중략...
ImageButton RotateButton = (ImageButton) findViewById(R.id.RotateButton);
RotateButton.setOnClickListener(new ImageButton.OnClickListener() {
    @Override
    public void onClick(Viec v) {
        A = A + 1;
    }
});

if (A % 2 == 0) // toggle
{
    headangle = headangle + 2.0f; // Cruise 회전 (머리 방향 회전)
    
    if (headangle >= 360.0f) headangle = 0.0f;
}
```  
> ImageTargetRenderer.java 일부분  
