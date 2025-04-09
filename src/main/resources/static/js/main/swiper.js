const swiper = new Swiper(".swiper", {
  slidesPerView: 3,
  spaceBetween: 0,
  loop: true,
  initialSlide: 0,
  centeredSlides: true,
  navigation: {
    nextEl: ".swiper-button-next",
    prevEl: ".swiper-button-prev",
  },
  on: {
    slideChangeTransitionEnd: function () {
      document.querySelectorAll(".swiper-slide img").forEach((img) => {
        img.style.transform = "scale(1)";
      });

      const activeSlide = document.querySelector(".swiper-slide-active img");
      if (activeSlide) {
        activeSlide.style.transform = "scale(1.3)";
      }
    },
    slideChange: function () {
      const currentSlide = this.slides[this.activeIndex]; // 여기 수정
      const imageName = currentSlide.getAttribute('data-name');
      if (imageName) {
        document.getElementById('point').innerHTML = `[&emsp;${imageName}&emsp;]`;
      }
    },
  },
});

window.onload = function () {
  const activeSlide = document.querySelector(".swiper-slide-active img");
  if (activeSlide) {
    activeSlide.style.transform = "scale(1.3)";
  }
};
