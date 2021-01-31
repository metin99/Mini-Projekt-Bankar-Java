-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 15, 2020 at 06:28 PM
-- Server version: 10.4.8-MariaDB
-- PHP Version: 7.3.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bank_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `action_history`
--

CREATE TABLE `action_history` (
  `client_id` varchar(15) NOT NULL,
  `name` varchar(25) NOT NULL,
  `surname` varchar(25) NOT NULL,
  `action` varchar(50) NOT NULL,
  `sum` double DEFAULT 0,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `action_history`
--

INSERT INTO `action_history` (`client_id`, `name`, `surname`, `action`, `sum`, `date`) VALUES
('a12345678b', 'Ainshtain', 'Ainshtain', 'Llogari e re', 2123, '2020-07-15');

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `id` varchar(15) NOT NULL,
  `name` varchar(25) NOT NULL,
  `surname` varchar(25) NOT NULL,
  `balance` double DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `freezed` int(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`id`, `name`, `surname`, `balance`, `phone`, `freezed`) VALUES
('a12345678b', 'Ainshtain', 'Ainshtain', 2123, '0049            ', 0);

-- --------------------------------------------------------

--
-- Table structure for table `loan`
--

CREATE TABLE `loan` (
  `loan_id` int(11) NOT NULL,
  `client_id` varchar(15) NOT NULL,
  `principal_amount` double NOT NULL,
  `interest_rate` double NOT NULL,
  `no_years` int(11) NOT NULL,
  `no_payments_yearly` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `current_payment_counter` int(11) DEFAULT 0,
  `no_irregular_payments` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `loan_history`
--

CREATE TABLE `loan_history` (
  `client_id` varchar(15) NOT NULL,
  `principal_amount` double NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `no_irregular_payments` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `loan`
--
ALTER TABLE `loan`
  ADD PRIMARY KEY (`loan_id`,`client_id`),
  ADD KEY `client_id` (`client_id`);

--
-- Indexes for table `loan_history`
--
ALTER TABLE `loan_history`
  ADD PRIMARY KEY (`client_id`,`start_date`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `loan`
--
ALTER TABLE `loan`
  MODIFY `loan_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `loan`
--
ALTER TABLE `loan`
  ADD CONSTRAINT `loan_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
