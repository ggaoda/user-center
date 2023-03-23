import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import {LINK1} from "@/constant";
const Footer: React.FC = () => {
  const defaultMessage = 'Gundam出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'My Csdn',
          title: '我的CSDN',
          href: LINK1,
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined />我的 Github</>,
          href: 'https://github.com/ggaoda',
          blankTarget: true,
        },
        {
          key: 'QQ Zone',
          title: 'QQ Zone',
          href: 'https://user.qzone.qq.com/1719923044/',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
