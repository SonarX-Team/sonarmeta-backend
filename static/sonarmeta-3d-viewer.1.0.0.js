// © 2022 SONARMETA 声呐元
// SonarX声呐探索, SonarX .Inc
const sonarmeta = (a, b, c) => {
  c.src = "https://www.sonarmeta.com" + "/3d-viewer/resource" + `/${b}` + `?secretKey=${a}`;
};
export default sonarmeta;
